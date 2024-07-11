package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemId(Integer itemId);

    @Query("select c " +
            "from Comment as c " +
            "join c.item as i " +
            "where i in ?1")
    List<Comment> findAllByItems(List<Item> items);
}
