package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.exception.NotValidationException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemBookDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    CommentRepository commentRepository;
    BookingRepository bookingRepository;

    @Override
    public ItemDto createItem(Integer ownerId, ItemDto itemDto) {
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: %d  не существует", ownerId)));
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user));
        return toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer ownerId, Integer itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id:%d не существует", itemId)));
        User user = userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: %d  не существует", ownerId)));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NotValidationException(String.format("Пользователь с id: %d не является владельцем вещи с id: %d",
                    user.getId(), item.getId()));
        }
        if (itemDto.getName() != null) {
            if (!itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            } else {
                throw new NotValidationException("Валидация по имени не пройдена");
            }
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getRequestId() != null) {
            if (itemDto.getRequestId() > 0) {
                item.setRequestId(itemDto.getRequestId());
            } else {
                throw new NotValidationException("Валидация по id запроса не пройдена");
            }
        }
        itemDto.setId(itemId);
        itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    public ItemBookDto getItemById(Integer ownerId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id: %d не существует.", itemId)));
        ItemBookDto itemBookDto;
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        List<CommentDto> comments = toCommentDto(commentRepository.findAllByItemId(itemId));
        itemBookDto = toItemBookDto(item);
        if (item.getOwner().getId().equals(ownerId) && !bookings.isEmpty()) {
            findLastAndNextBooking(itemBookDto, bookings);
        }
        itemBookDto.setComments(comments);

        return itemBookDto;
    }

    @Override
    public List<ItemBookDto> getItemsForUser(RequestItem requestItem) {
        Integer userId = requestItem.getUserId();
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom() / requestItem.getSize(), requestItem.getSize());

        List<Item> items = itemRepository.findAllByOwnerId(userId, pageRequest);
        List<Booking> bookings = bookingRepository.findAllByItems(items);
        List<CommentDto> comments = toCommentDto(commentRepository.findAllByItems(items));
        List<ItemBookDto> itemBookDtoList = new ArrayList<>();

        for (Item currentItem : items) {
            Integer itemId = currentItem.getId();
            ItemBookDto itemBookDto = toItemBookDto(currentItem);

            List<Booking> bookingsForItem = bookings.stream()
                    .filter(booking -> itemId.equals(booking.getItem().getId()))
                    .collect(Collectors.toList());

            List<CommentDto> commentsForItem = comments.stream()
                    .filter(commentDto -> itemId.equals(commentDto.getItemId()))
                    .collect(Collectors.toList());

            if (userId.equals(currentItem.getOwner().getId()) && !bookings.isEmpty()) {
                findLastAndNextBooking(itemBookDto, bookingsForItem);
            }

            itemBookDto.setComments(commentsForItem);
            itemBookDtoList.add(itemBookDto);
        }
        return itemBookDtoList;
    }

    @Override
    public List<ItemDto> search(RequestItem item) {
        String text = item.getText();
        PageRequest pageRequest = PageRequest.of(item.getFrom() / item.getSize(), item.getSize());

        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true, text,
                        text, pageRequest);
        return ItemMapper.toItemDtoList(items);
    }

    @Override
    public CommentDto addComment(CommentDto commentDto, Integer userId, Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundItemException(String.format("Вещь с id: %d отсутствует", itemId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(""));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotValidationException("Владелец не может оставить отзыв");
        }

        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now());

        bookings.stream()
                .filter((booking) -> booking.getBooker().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotValidationException("Пользователь с id: " + userId +
                        " не может взять в аренду вещь с  id: " + itemId));

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, user, item));
        return toCommentDto(comment);
    }

    @Override
    public ItemDto deleteItem(Integer itemId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NotFoundUserException(String.format("Пользователя с id: %d не существует", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundItemException(String.format("Вещи с id: %d не существует", itemId)));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new NotValidationException(String.format("Пользователь с id: %d не является владельцем вещи с id: %d",
                    user.getId(), item.getId()));
        }
        itemRepository.delete(item);
        return toItemDto(item);
    }

    private void findLastAndNextBooking(ItemBookDto item, List<Booking> bookings) {
        Booking lastBooking = bookings.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && !Status.REJECTED.equals(booking.getStatus()))
                .min(Booking::compareTo)
                .orElse(null);

        Booking nextBooking = bookings.stream()
                .filter((booking) -> booking.getStart().isAfter(LocalDateTime.now()) && !Status.REJECTED.equals(booking.getStatus()))
                .max(Booking::compareTo)
                .orElse(null);

        item.setLastBooking(lastBooking == null ? null : toBookingDto(lastBooking));
        item.setNextBooking(nextBooking == null ? null : toBookingDto(nextBooking));
    }
}
