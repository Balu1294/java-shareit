package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByAvailableAndDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(boolean available,
                                                                                              String description,
                                                                                              String name);

    List<Item> findAllByOwnerId(Integer userId);

    List<Item> findAllByRequestId(Integer requestId);

    @Query("select i from Item as i " +
            "where i.requestId in ?1")
    List<Item> findAllByRequests(List<Integer> requests);

}
