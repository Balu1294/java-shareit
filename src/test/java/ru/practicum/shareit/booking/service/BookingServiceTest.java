package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.exception.NotFoundBookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exception.NotValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exception.NotFoundUserException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.mapper.BookingMapper.toBookingDto;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    int userId;
    int itemId;
    int ownerId;
    int bookingId;
    User booker;
    User owner;
    Item item;
    Booking booking;
    RequestBooking requestBooking;
    LocalDateTime now;

    @BeforeEach
    public void setUp() {
        now = LocalDateTime.now();
        userId = 1;
        itemId = 1;
        bookingId = 1;
        ownerId = 2;

        owner = User.builder()
                .id(ownerId)
                .email("google@mail.ru")
                .name("Bon Jon")
                .build();

        booker = User.builder()
                .id(userId)
                .email("mail@mail.ru")
                .name("Jon Bon")
                .build();

        item = Item.builder()
                .id(itemId)
                .name("Отвертка")
                .description("Простая отвертка")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(bookingId)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .end(now.plusDays(1))
                .start(now.plusHours(2))
                .build();

        requestBooking = requestBooking.builder()
                .from(0)
                .size(10)
                .userId(userId)
                .build();
    }

    @Test
    public void addBookingWhenInvokedMethodReturnBooking() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertEquals(toBookingDto(booking), bookingService.createBooking(toBookingDto(booking), userId));
    }

    @Test
    public void addBookingWhenBookerIsOwnerTheItemThrowException() {
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundBookingException.class, () -> bookingService.createBooking(toBookingDto(booking), ownerId));
    }

    @Test
    public void addBookingWhenItemIsNotAvailableThrowException() {
        item.setAvailable(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotValidationException.class, () -> bookingService.createBooking(toBookingDto(booking), userId));
    }

    @Test
    public void addBookingWhenStartTimeIsBeforeThanEndTimeThrowException() {
        booking.setStart(LocalDateTime.now().plusHours(2));
        booking.setEnd(LocalDateTime.now().plusHours(1));

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotValidationException.class, () -> bookingService.createBooking(toBookingDto(booking), userId));
    }

    @Test
    public void deleteBookingWhenInvokedMethodReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals(toBookingDto(booking), bookingService.removeBooking(bookingId, userId));
    }

    @Test
    public void deleteBookingWhenUserIsNotTheOwnerTheBookingThrowException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundUserException.class, () -> bookingService.removeBooking(bookingId, ownerId));
    }

    @Test
    public void deleteBookingWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(NotFoundBookingException.class);

        assertThrows(NotFoundBookingException.class, () -> bookingService.removeBooking(bookingId, userId));
    }

    @Test
    public void getBookingByIdWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(NotFoundBookingException.class);

        assertThrows(NotFoundBookingException.class, () -> bookingService.removeBooking(bookingId, userId));
    }

    @Test
    public void getBookingByIdWhenInvokedMethodReturnBooking() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertEquals(toBookingDto(booking), bookingService.getBookingById(userId, bookingId));
    }

    @Test
    public void setApproveWhenBookingAlreadyApprovedThrowException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotValidationException.class, () -> bookingService.setApprove(bookingId, true, ownerId));
    }

    @Test
    public void setApproveWhenUserNotTheOwnerTheItemThrowException() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundUserException.class, () -> bookingService.setApprove(bookingId, true, userId));
    }

    @Test
    public void setApproveWhenInvokedMethodReturnBooking() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto foundBooking = bookingService.setApprove(bookingId, true, ownerId);
        booking.setStatus(Status.APPROVED);

        assertEquals(toBookingDto(booking), foundBooking);
    }

    @Test
    public void setApproveWhenBookingNotFoundThrowException() {
        when(bookingRepository.findById(bookingId)).thenThrow(NotFoundBookingException.class);

        assertThrows(NotFoundBookingException.class, () -> bookingService.setApprove(bookingId, true, ownerId));
    }

    @Test
    public void setApproveWhenApprovedFalseReturnBooking() {
        booking.setStatus(Status.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto returnedBooking = bookingService.setApprove(bookingId, false, ownerId);

        assertEquals(toBookingDto(booking), returnedBooking);
        assertEquals(Status.REJECTED, returnedBooking.getStatus());
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsAllReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toBookingDto(booking));
        requestBooking.setState("ALL");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdOrderByEndDesc(any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingByUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsCurrentReturnEmptyList() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toBookingDto(booking));
        requestBooking.setState("CURRENT");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByUserUserIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(any(),
                any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingByUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsPastReturnOneBooking() {
        Booking pastBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .endTime(now.minusDays(2))
                .startTime(now.minusDays(1))
                .build();

        List<Booking> bookings = List.of(pastBooking);
        List<BookingDto> foundBookings = List.of(toDto(pastBooking));
        requestBooking.setState("Past");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsFutureReturnOneBooking() {
        Booking futureBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(futureBooking);
        List<BookingDto> foundBookings = List.of(toDto(futureBooking));
        requestBooking.setState("Future");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsWaitingReturnOneBooking() {
        Booking waitingBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(waitingBooking);
        List<BookingDto> foundBookings = List.of(toDto(waitingBooking));
        requestBooking.setState("Waiting");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(any(), any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsRejectedReturnOneBooking() {
        Booking rejectedBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.REJECTED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(rejectedBooking);
        List<BookingDto> foundBookings = List.of(toDto(rejectedBooking));
        requestBooking.setState("REJECTED");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByUserUserIdAndStatusOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForCurrentUserWhenStateIsNotValidReturnOneBooking() {
        requestBooking.setState("UNKNOWN");

        when(userRepository.findById(userId)).thenReturn(Optional.of(booker));

        assertThrows(NotValidStateException.class, () -> bookingService.getBookingForCurrentUser(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsAllReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("ALL");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllBookingByOwnerId(any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsCurrentReturnOneBooking() {
        List<Booking> bookings = List.of(booking);
        List<BookingDto> foundBookings = List.of(toDto(booking));
        requestBooking.setState("CURRENT");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc(any(),
                any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsPastReturnOneBooking() {
        Booking pastBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .endTime(now.minusDays(2))
                .startTime(now.minusDays(1))
                .build();

        List<Booking> bookings = List.of(pastBooking);
        List<BookingDto> foundBookings = List.of(toDto(pastBooking));
        requestBooking.setState("Past");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsFutureReturnOneBooking() {
        Booking futureBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.APPROVED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(futureBooking);
        List<BookingDto> foundBookings = List.of(toDto(futureBooking));
        requestBooking.setState("Future");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsWaitingReturnOneBooking() {
        Booking waitingBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.WAITING)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(waitingBooking);
        List<BookingDto> foundBookings = List.of(toDto(waitingBooking));
        requestBooking.setState("Waiting");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDesc(any(), any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsRejectedReturnOneBooking() {
        Booking rejectedBooking = Booking.builder()
                .bookingId(bookingId)
                .item(item)
                .user(booker)
                .status(Status.REJECTED)
                .endTime(now.plusDays(2))
                .startTime(now.plusDays(1))
                .build();

        List<Booking> bookings = List.of(rejectedBooking);
        List<BookingDto> foundBookings = List.of(toDto(rejectedBooking));
        requestBooking.setState("REJECTED");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemUserUserIdAndStatusOrderByEndTimeDesc(any(), any(), any())).thenReturn(bookings);

        assertEquals(foundBookings, bookingService.getBookingForOwner(requestBooking));
    }

    @Test
    public void getBookingForOwnerWhenStateIsNotValidReturnOneBooking() {
        requestBooking.setState("UNKNOWN");
        requestBooking.setUserId(ownerId);

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));

        assertThrows(NotValidationException.class, () -> bookingService.getBookingForOwner(requestBooking));
    }
}
