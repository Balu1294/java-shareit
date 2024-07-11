package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.NotFoundItemException;
import ru.practicum.shareit.item.exception.NotValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.RequestItem;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.user.mapper.UserMapper.toUser;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemServiceImplTest {
    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    ItemBookDto itemBookDto;
    UserDto userDto;
    ItemDto itemDto;
    int itemId;
    int userId;

    @BeforeEach
    public void setUp() {
        itemId = 1;
        userId = 1;
        userDto = new UserDto(userId, "Jon Bon", "mail@mail.ru");
        itemBookDto = new ItemBookDto(itemId, "Отвертка", userDto, "Классная отвертка", true);
        itemBookDto.setComments(new ArrayList<>());
        itemDto = new ItemDto(itemId, "Стремянка", "Высокая стремянка", true, userDto.getId(), 1);
    }

    @Test
    public void getItemByIdWhenItemFoundReturnItem() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));

        ItemBookDto foundItem = itemService.getItemById(itemId, userId);

        assertEquals(itemBookDto, foundItem);
        verify(bookingRepository).findAllByItemId(itemId);
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    public void getItemByIdWhenUserIsOwnerTheItemReturnItemWithBookings() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, currentUser, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, currentUser, Status.APPROVED);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(bookingRepository.findAllByItemId(itemId)).thenReturn(List.of(lastBooking, nextBooking));

        ItemBookDto foundItem = itemService.getItemById(itemId, userId);
        itemBookDto.setLastBooking(toBookingDto(lastBooking));
        itemBookDto.setNextBooking(toBookingDto(nextBooking));

        assertEquals(itemBookDto, foundItem);
        verify(bookingRepository).findAllByItemId(itemId);
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    public void getItemByIdWhenItemNotExistsThrowException() {
        when(itemRepository.findById(itemId)).thenThrow(NotFoundItemException.class);

        assertThrows(NotFoundItemException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasBookingsAndCommentsReturnItemWithBookingsAndComments() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, currentUser, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, currentUser, Status.APPROVED);
        Comment comment = new Comment(1, "text", item, currentUser, 1, LocalDateTime.now().minusDays(1));
        List<Booking> bookings = List.of(lastBooking, nextBooking);
        List<Comment> comments = List.of(comment);

        itemBookDto.setLastBooking(toBookingDto(lastBooking));
        itemBookDto.setNextBooking(toBookingDto(nextBooking));
        itemBookDto.setComments(toCommentDto(comments));

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, currentUser)));

        assertEquals(itemBookDto, itemService.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasOneBookingReturnWithBookingAndComments() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, currentUser, Status.APPROVED);
        Comment comment = new Comment(1, "text", item, currentUser, 1, LocalDateTime.now().minusDays(1));

        List<Booking> bookings = List.of(lastBooking);
        List<Comment> comments = List.of(comment);

        itemBookDto.setLastBooking(toBookingDto(lastBooking));
        itemBookDto.setComments(toCommentDto(comments));

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, currentUser)));

        assertEquals(itemBookDto, itemService.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasBookingsReturnItemWithBookings() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);

        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, currentUser, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, currentUser, Status.APPROVED);
        List<Booking> bookings = List.of(lastBooking, nextBooking);

        itemBookDto.setLastBooking(toBookingDto(lastBooking));
        itemBookDto.setNextBooking(toBookingDto(nextBooking));

        when(bookingRepository.findAllByItemId(itemId)).thenReturn(bookings);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, currentUser)));

        assertEquals(itemBookDto, itemService.getItemById(itemId, userId));
    }

    @Test
    public void getItemByIdWhenItemHasCommentsReturnItemWithComments() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);
        Comment comment = new Comment(1, "text", item, currentUser, 1, LocalDateTime.now().minusDays(1));

        List<Comment> comments = List.of(comment);

        itemBookDto.setComments(toCommentDto(comments));

        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, currentUser)));

        assertEquals(itemBookDto, itemService.getItemById(itemId, userId));
    }

    @Test
    public void getItemsForUserWhenFoundOneItemReturnItem() {
        Item item = toItem(itemBookDto, toUser(userDto));
        User currentUser = toUser(userDto);
        RequestItem requestItem = RequestItem.of(1, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, currentUser, Status.APPROVED);
        Booking nextBooking = new Booking(2, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, currentUser, Status.APPROVED);
        Comment comment = new Comment(1, "text", item, currentUser, 1, LocalDateTime.now().minusDays(1));

        List<Booking> bookings = List.of(lastBooking, nextBooking);
        List<Comment> comments = List.of(comment);

        itemBookDto.setLastBooking(toBookingDto(lastBooking));
        itemBookDto.setNextBooking(toBookingDto(nextBooking));
        itemBookDto.setComments(toCommentDto(comments));

        List<Item> items = List.of(toItem(itemBookDto, toUser(userDto)));
        List<ItemBookDto> itemsDto = List.of(itemBookDto);

        when(bookingRepository.findAllByItems(items)).thenReturn(bookings);
        when(commentRepository.findAllByItems(items)).thenReturn(comments);
        when(itemRepository.findAllByOwnerId(userId, pageRequest)).thenReturn(items);

        List<ItemBookDto> foundItems = itemService.getItemsForUser(requestItem);

        assertEquals(itemsDto, foundItems);
        assertEquals(1, foundItems.size());
    }

    @Test
    public void getItemsForUserWhenItemNotFoundReturnEmptyList() {
        List<ItemBookDto> items = List.of();
        RequestItem requestItem = RequestItem.of(1, 0, 10, "text");

        List<ItemBookDto> foundItems = itemService.getItemsForUser(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void updateItemWhenSetNewNameAndDescriptionAndRequestIdAndAvailableReturnUpdatedItem() {
        ItemDto newItem = new ItemDto(1, "updated", "new description", true,
                userDto.getId(), 1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = itemService.updateItem(userId, itemId, newItem);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getName(), updatedItem.getName());
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        assertEquals(newItem.getOwnerId(), updatedItem.getOwnerId());
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        assertEquals(newItem.getRequestId(), updatedItem.getRequestId());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewNameReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setName("name");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = itemService.updateItem(userId, itemId, newItem);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getName(), updatedItem.getName());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewDescriptionReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setDescription("description");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = itemService.updateItem(userId, itemId, newItem);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getDescription(), updatedItem.getDescription());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewAvailableReturnUpdatedItem() {
        ItemDto newItem = new ItemDto();
        newItem.setAvailable(false);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        ItemDto updatedItem = itemService.updateItem(userId, itemId, newItem);

        assertEquals(newItem, updatedItem);
        assertEquals(newItem.getAvailable(), updatedItem.getAvailable());
        verify(itemRepository).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewBlankNameReturnItemWithOldName() {
        ItemDto newItem = new ItemDto();
        newItem.setName(" ");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        assertThrows(NotValidationException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenSetNewBadRequestIdReturnItemWithOldName() {
        ItemDto newItem = new ItemDto();
        newItem.setRequestId(-1);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));

        assertThrows(NotValidationException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenUserNotFoundThrowException() {
        int userId = 0;
        ItemDto newItem = new ItemDto();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenItemNotFoundThrowException() {
        int itemId = 0;
        ItemDto newItem = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void updateItemWhenUserIsNotOwnerTheItemThrowException() {
        int userId = 2;
        User newUser = new User();
        newUser.setId(userId);
        ItemDto newItem = new ItemDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemBookDto, toUser(userDto))));
        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));

        assertThrows(NotValidationException.class, () -> itemService.updateItem(userId, itemId, newItem));
        verify(itemRepository, never()).save(toItem(newItem, toUser(userDto)));
    }

    @Test
    public void addItemWhenMethodInvokedReturnItem() {
        Item currentItem = toItem(itemDto, toUser(userDto));

        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.save(currentItem)).thenReturn(currentItem);

        assertEquals(itemDto, itemService.createItem(userId, itemDto));
        verify(itemRepository).save(currentItem);
    }

    @Test
    public void addItemWhenUserNotFoundReturnItem() {
        int userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> itemService.createItem(userId, itemDto));
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void deleteItemWhenMethodInvokedReturnDeletedItem() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemDto, toUser(userDto))));

        assertEquals(itemDto, itemService.deleteItem(itemId, userId));
        verify(itemRepository).delete(any());
    }

    @Test
    public void deleteItemWhenUserIsNotOwnerTheItemThrowException() {
        int userId = 2;
        User newUser = new User();
        newUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(newUser));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(toItem(itemDto, toUser(userDto))));

        assertThrows(NotValidationException.class, () -> itemService.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void deleteItemWhenUserNotFoundThrowException() {
        int userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundUserException.class, () -> itemService.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void deleteItemWhenItemNotFoundThrowException() {
        int userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.of(toUser(userDto)));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundItemException.class, () -> itemService.deleteItem(itemId, userId));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    public void searchItemWhenInvokedMethodReturnOneItem() {
        RequestItem requestItem = RequestItem.of(1, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        List<ItemDto> items = List.of(itemDto);

        when(itemRepository.findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                requestItem.getText(), requestItem.getText(), pageRequest)).thenReturn(List.of(toItem(itemDto, toUser(userDto))));

        assertEquals(items, itemService.search(requestItem));
    }

    @Test
    public void searchItemWhenNotFoundItemsReturnEmptyList() {
        RequestItem requestItem = RequestItem.of(1, 0, 10, "text");
        PageRequest pageRequest = PageRequest.of(requestItem.getFrom(), requestItem.getSize());
        List<ItemDto> items = List.of();

        when(itemRepository.findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(true,
                requestItem.getText(), requestItem.getText(), pageRequest)).thenReturn(List.of());

        List<ItemDto> foundItems = itemService.search(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void searchItemWhenRequestTextIsBlankReturnEmptyList() {
        RequestItem requestItem = RequestItem.of(1, 0, 10, "");
        List<ItemDto> items = List.of();

        List<ItemDto> foundItems = itemService.search(requestItem);

        assertEquals(items, foundItems);
        assertEquals(0, foundItems.size());
    }

    @Test
    public void addCommentWhenMethodInvokeReturnComment() {
        int userId = 2;
        Item item = toItem(itemDto, toUser(userDto));
        userDto.setId(userId);
        User user = toUser(userDto);
        CommentDto commentDto = new CommentDto(1, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);
        Comment comment = toComment(commentDto, user, item);
        Booking lastBooking = new Booking(1, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), item, user, Status.APPROVED);
        List<Booking> bookings = List.of(lastBooking);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(anyInt(), any()))
                .thenReturn(bookings);
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto commentDto1 = itemService.addComment(commentDto, userId, itemId);
        assertEquals(commentDto, commentDto1);
    }

    @Test
    public void addCommentWhenUserIsOwnerOfTheItemThrowException() {
        User user = toUser(userDto);
        Item item = toItem(itemDto, user);
        CommentDto commentDto = new CommentDto(1, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotValidationException.class, () -> itemService.addComment(commentDto, userId, itemId));
    }

    @Test
    public void addCommentWhenUserDidNotRentTheItemThrowException() {
        Integer userId = 2;
        Item item = toItem(itemDto, toUser(userDto));
        userDto.setId(userId);
        User user = toUser(userDto);
        CommentDto commentDto = new CommentDto(1, userDto, userDto.getName(), userId, 1, "text",
                LocalDateTime.now(), itemDto, itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotValidationException.class, () -> itemService.addComment(commentDto, userId, itemId));
    }
}