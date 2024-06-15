package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enumStatus.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("select b from Booking b " +
            "join b.item i " +
            "join i.owner o " +
            "where o.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingByUserId(Integer userId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndTimeDesc(Integer userId, LocalDateTime one,
                                                                                      LocalDateTime two);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime time);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByEndDesc(Integer userId, LocalDateTime time,
                                                                             Status status);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(Integer userId, Status status);

    List<Booking> findAllByBookerIdOrderByEndDesc(Integer userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Integer userId, LocalDateTime one, LocalDateTime two);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(Integer userId, LocalDateTime time, Status status);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(Integer userId, Status status);

}
