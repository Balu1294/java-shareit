package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
@Transactional
@DataJpaTest
@Sql(value = {"/set-up-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {"/set-up-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class BookingRepositoryIntegrationTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    private int userId;
    private int ownerId;
    private PageRequest defaultPageRequest;
    private LocalDateTime now;

    @BeforeEach
    public void setUp() {
        ownerId = 1;
        userId = 2;
        defaultPageRequest = PageRequest.of(0, 10);
        now = LocalDateTime.now();
    }

    @Test
    public void findAllByUserUserIdOrderByEndTimeDescWhenFoundTwoBookingsReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userId, defaultPageRequest);

        assertEquals(4, bookings.size());
        assertEquals(1, bookings.get(3).getId());
        assertEquals(3, bookings.get(2).getId());
        assertEquals(2, bookings.get(1).getId());
        assertEquals(4, bookings.get(0).getId());
    }

    @Test
    public void findAllByUserUserIdOrderByEndTimeDescWhenBookingsNotFoundReturnEmptyList() {
        int userIdWithoutBookings = 1;

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(userIdWithoutBookings, defaultPageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    public void findAllBookingByOwnerIdWhenInvokedMethodReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findBookingByUserId(ownerId, defaultPageRequest);

        assertEquals(4, bookings.size());
    }

    @Test
    public void findAllBookingByOwnerIdWhenOwnerDoesntHaveBookingsReturnEmptyList() {
        int ownerIdWithoutBookings = 2;

        List<Booking> bookings = bookingRepository.findBookingByUserId(ownerIdWithoutBookings, defaultPageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(userId,
                        now, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterOrderByEndTimeDescWhenInvokedMethodReturnTwoBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartIsAfterOrderByEndDesc(userId, now, defaultPageRequest);

        assertEquals(2, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, now, Status.APPROVED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsCanceledReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(userId, now, Status.CANCELED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStartTimeIsAfterAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnOneBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByEndDesc(ownerId, now, Status.APPROVED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndStatusOrderByEndTimeDescWhenInvokedMethodReturnThreeBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByEndDesc(userId, Status.APPROVED, defaultPageRequest);

        assertEquals(3, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndEndIsBeforeOrderByEndDesc(userId, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodWithoutPageRequestReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndEndIsBeforeOrderByEndDesc(userId, now);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStartTimeIsBeforeAndEndTimeIsAfterOrderByEndTimeDesc() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStatusOrderByEndTimeDescWhenStatusIsApprovedReturnThreeBookings() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, Status.APPROVED, defaultPageRequest);

        assertEquals(3, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndStatusOrderByEndTimeDescWhenStatusIsCanceledReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, Status.CANCELED, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemUserUserIdAndEndTimeIsBeforeOrderByEndTimeDescWhenInvokedMethodReturnOneBooking() {
        List<Booking> bookings = bookingRepository
                .findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now, defaultPageRequest);

        assertEquals(1, bookings.size());
    }

    @Test
    public void findAllByItemItemIdWhenMethodInvokedReturnFourBookings() {
        List<Booking> bookings = bookingRepository.findAllByItemId(1);

        assertEquals(4, bookings.size());
    }

    @Test
    public void findAllByItemItemIdWhenItemDoesntHaveBookingsReturnEmptyList() {
        List<Booking> bookings = bookingRepository.findAllByItemId(2);

        assertEquals(0, bookings.size());
    }
}
