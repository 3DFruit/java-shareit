package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@DataJpaTest
public class ItemRequestStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRequestStorage itemRequestStorage;

    private final User user1 = new User();
    private final User user2 = new User();
    private final User user3 = new User();

    private void populateData() {
        user1.setName("name");
        user1.setEmail("mail@mail.com");
        user2.setName("name");
        user2.setEmail("test@mail.com");
        user3.setName("name");
        user3.setEmail("testmail@mail.com");
        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("request1");
        itemRequest1.setRequester(user1);
        itemRequest1.setCreated(LocalDateTime.now());
        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("request2");
        itemRequest2.setRequester(user2);
        itemRequest2.setCreated(LocalDateTime.now());
        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setDescription("request3");
        itemRequest3.setRequester(user3);
        itemRequest3.setCreated(LocalDateTime.now());
        em.persist(itemRequest1);
        em.persist(itemRequest2);
        em.persist(itemRequest3);
    }

    @Test
    void findByRequesterTest() {
        populateData();
        Collection<ItemRequest> itemRequests = itemRequestStorage.findByRequesterIsOrderByCreatedDesc(
                user1);
        Assertions.assertEquals(itemRequests.size(), 1);
        Assertions.assertTrue(itemRequests.stream().allMatch(
                (ItemRequest itemRequest) -> itemRequest.getRequester().getId().equals(user1.getId())));
    }

    @Test
    void findByRequesterIsNotTest() {
        populateData();
        Page<ItemRequest> itemRequests = itemRequestStorage.findAllByRequesterIsNotOrderByCreatedDesc(
                user1, PageRequest.of(0, 20));
        Assertions.assertEquals(itemRequests.getTotalElements(), 2);
        Assertions.assertTrue(itemRequests.stream().noneMatch(
                (ItemRequest itemRequest) -> itemRequest.getRequester().getId().equals(user1.getId())));
    }
}
