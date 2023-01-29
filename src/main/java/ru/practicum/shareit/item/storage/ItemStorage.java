package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

public interface ItemStorage extends JpaRepository<Item, Long> {
    //Collection<Item> searchItems(String text);
}
