package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Repository
public interface CommentStorage extends JpaRepository<Comment, Long> {
    Collection<Comment> findByItemIsOrderByCreatedDesc(Item item);
}
