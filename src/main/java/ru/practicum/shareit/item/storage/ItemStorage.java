package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerIs(User owner);

    @Query("SELECT i FROM Item as i " +
            "WHERE (upper(i.name) like upper(CONCAT('%', ?1, '%')) " +
            "or upper(i.description) like upper(CONCAT('%', ?1, '%')))" +
            "and i.isAvailable = true")
    Collection<Item> searchItems(String text);
}
