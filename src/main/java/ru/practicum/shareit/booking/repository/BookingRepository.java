package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enumStatus.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("select b from Booking b " +
            "join b.item i " +
            "join i.owner o " +
            "where o.id = ?1 " +
            "order by b.start desc")
    List<Booking> findBookingByUserId(Integer userId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Integer userId, LocalDateTime one,
                                                                                  LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterAndStatusOrderByEndDesc(Integer userId, LocalDateTime time,
                                                                             Status status, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(Integer userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdOrderByEndDesc(Integer userId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Integer userId, LocalDateTime one, LocalDateTime two, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByEndDesc(Integer userId, LocalDateTime time);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByEndDesc(Integer userId, LocalDateTime time, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartIsAfterAndStatusOrderByEndDesc(Integer userId, LocalDateTime time, Status status, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(Integer userId, Status status, PageRequest pageRequest);

    List<Booking> findAllByItemId(Integer itemId);

    @Query("select b " +
            "from Booking as b " +
            "join b.item as i " +
            "where i in ?1")
    List<Booking> findAllByItems(List<Item> items);
}
