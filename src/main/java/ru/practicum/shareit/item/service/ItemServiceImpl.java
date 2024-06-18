package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundUserException(String.format("Пользователя с id: %d  не существует", ownerId)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundItemException(""));
        if (itemDto.getId() != null) {
            item.setId(itemDto.getId());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        item = itemRepository.save(item);
        return toItemDto(item);
    }

    @Override
    public ItemBookDto getItemById(Integer ownerId, Integer itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundItemException(String.format("Вещи с id: %d не существует.", itemId)));
        ItemBookDto itemBookDto;
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        List<CommentDto> comments = CommentMapper.toCommentDto(commentRepository.findAllCommentByItemId(itemId));


        if (item.getOwner().getId().equals(ownerId) && !bookings.isEmpty()) {
            Booking lastBooking = bookings.stream()
                    .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()) && !booking.getStatus().equals(Status.REJECTED))
                    .min(Booking::compareTo)
                    .orElse(null);

            Booking nextBooking = bookings.stream()
                    .filter((booking) -> booking.getStart().isAfter(LocalDateTime.now()) && !booking.getStatus().equals(Status.REJECTED))
                    .max(Booking::compareTo)
                    .orElse(null);

            itemBookDto = ItemMapper.toItemBookDto(item, (lastBooking == null ? null : BookingMapper.toBookingDto(lastBooking)),
                    (nextBooking == null ? null : BookingMapper.toBookingDto(nextBooking)), comments);
        } else {
            itemBookDto = ItemMapper.toItemBookDto(item, null, null, comments);
        }
        return itemBookDto;
    }

    @Override
    public List<ItemBookDto> getAllItems(Integer ownerId) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundUserException(""));
        List<Item> items = itemRepository.findAll();
        List<ItemBookDto> itemsfiltr = items.stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(item -> getItemById(ownerId, item.getId()))
                .collect(Collectors.toList());
        return itemsfiltr;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository
                .findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true, text, text);
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
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<ItemDto> getItemsForUser(Integer userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        return items.stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(item -> toItemDto(item))
                .collect(Collectors.toList());
    }
}
