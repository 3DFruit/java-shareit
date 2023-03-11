package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getItemsTest() {
        UserDto user1dto = userService.createUser(new UserDto(null, "user1", "user1@mail.com"));
        UserDto user2dto = userService.createUser(new UserDto(null, "user2", "user2@mail.com"));
        itemService.addItem(user1dto.getId(), new ItemDto(
                null,
                "item1",
                "description",
                true,
                null,
                null,
                null,
                null
        ));
        itemService.addItem(user1dto.getId(), new ItemDto(
                null,
                "item2",
                "description",
                true,
                null,
                null,
                null,
                null
        ));
        itemService.addItem(user2dto.getId(), new ItemDto(
                null,
                "item3",
                "description",
                true,
                null,
                null,
                null,
                null
        ));
        itemService.addItem(user1dto.getId(), new ItemDto(
                null,
                "item4",
                "description",
                true,
                null,
                null,
                null,
                null
        ));
        itemService.addItem(user2dto.getId(), new ItemDto(
                null,
                "item5",
                "description",
                true,
                null,
                null,
                null,
                null
        ));
        User user = em.createQuery("Select u from User u where u.id = :id", User.class)
                .setParameter("id", user1dto.getId())
                .getSingleResult();
        Collection<ItemDto> itemDtos = itemService.getItems(user1dto.getId(), 0, 20);
        Collection<Long> itemsIds = em.createQuery("Select i from Item i where i.owner = :owner", Item.class)
                .setParameter("owner", user)
                .getResultList()
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Assertions.assertNotEquals(0, itemDtos.size());
        Assertions.assertTrue(itemDtos.stream().map(ItemDto::getId).collect(Collectors.toList())
                        .containsAll(itemsIds)
                );
    }
}
