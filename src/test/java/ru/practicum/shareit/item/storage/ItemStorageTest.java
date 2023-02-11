package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@DataJpaTest
public class ItemStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemStorage itemStorage;

    private final Item testItem = new Item();
    private final User testUser = new User();
    private final ItemRequest itemRequest = new ItemRequest();

    @BeforeEach
    public void populateData() {
        testUser.setName("testUser");
        testUser.setEmail("testUser@mail.com");
        em.persist(testUser);
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@mail.com");
        em.persist(user1);
        itemRequest.setDescription("description");
        itemRequest.setRequester(user1);
        itemRequest.setCreated(LocalDateTime.now());
        em.persist(itemRequest);
        ItemRequest ir1 = new ItemRequest();
        ir1.setDescription("description");
        ir1.setRequester(user1);
        ir1.setCreated(LocalDateTime.now());
        em.persist(ir1);
        testItem.setName("item1");
        testItem.setDescription("description");
        testItem.setAvailable(true);
        testItem.setOwner(testUser);
        em.persist(testItem);
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("New description");
        item1.setAvailable(true);
        item1.setOwner(user1);
        em.persist(item1);
        Item item2 = new Item();
        item2.setName("item nEw");
        item2.setDescription("description");
        item2.setAvailable(true);
        item2.setOwner(user1);
        item2.setRequest(itemRequest);
        em.persist(item2);
        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("description");
        item3.setAvailable(true);
        item3.setOwner(user1);
        item3.setRequest(ir1);
        em.persist(item3);
    }

    @Test
    void findByOwnerTest() {
        Page<Item> items = itemStorage.findAllByOwnerIsOrderByIdAsc(
                testUser, PageRequest.of(0, 20));
        Assertions.assertEquals(1, items.getTotalElements());
        Assertions.assertTrue(items.stream().allMatch(
                (Item item) -> item.getOwner().getId().equals(testUser.getId())));
    }

    @Test
    void searchTest() {
        Page<Item> items = itemStorage.searchItems(
                "new", PageRequest.of(0, 20));
        Assertions.assertEquals(2, items.getTotalElements());
        Assertions.assertTrue(items.stream().allMatch(
                (Item item) -> item.getName().toLowerCase().contains("new")
                        || item.getDescription().toLowerCase().contains("new")));
    }

    @Test
    void findByRequestTest() {
        Collection<Item> items = itemStorage.findAllByRequestIn(List.of(itemRequest));
        Assertions.assertEquals(1, items.size());
        Assertions.assertTrue(items.stream().allMatch(
                (Item item) -> item.getRequest() != null
                        && item.getRequest().getId().equals(itemRequest.getId())));
    }
}
