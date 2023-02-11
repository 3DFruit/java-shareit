package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

@DataJpaTest
public class CommentStorageTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private CommentStorage commentStorage;

    private final Item testItem = new Item();

    private void populateData() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@mail.com");
        User author = new User();
        author.setName("author");
        author.setEmail("author@mail.com");
        em.persist(owner);
        em.persist(author);
        testItem.setName("testItem");
        testItem.setDescription("description");
        testItem.setAvailable(true);
        testItem.setOwner(owner);
        em.persist(testItem);
        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("description");
        item1.setAvailable(true);
        item1.setOwner(owner);
        em.persist(item1);
        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("description");
        item2.setAvailable(true);
        item2.setOwner(owner);
        em.persist(item2);
        Comment comment1 = new Comment();
        comment1.setItem(testItem);
        comment1.setAuthor(author);
        comment1.setText("comment1");
        comment1.setCreated(LocalDateTime.now());
        Comment comment2 = new Comment();
        comment2.setItem(testItem);
        comment2.setAuthor(author);
        comment2.setText("comment2");
        comment2.setCreated(LocalDateTime.now());
        Comment comment3 = new Comment();
        comment3.setItem(item1);
        comment3.setAuthor(author);
        comment3.setText("comment3");
        comment3.setCreated(LocalDateTime.now());
        Comment comment4 = new Comment();
        comment4.setItem(item2);
        comment4.setAuthor(author);
        comment4.setText("comment4");
        comment4.setCreated(LocalDateTime.now());
        Comment comment5 = new Comment();
        comment5.setItem(testItem);
        comment5.setAuthor(author);
        comment5.setText("comment5");
        comment5.setCreated(LocalDateTime.now());
        Comment comment6 = new Comment();
        comment6.setItem(item1);
        comment6.setAuthor(author);
        comment6.setText("comment6");
        comment6.setCreated(LocalDateTime.now());
        em.persist(comment1);
        em.persist(comment2);
        em.persist(comment3);
        em.persist(comment4);
        em.persist(comment5);
        em.persist(comment6);
    }

    @Test
    void findByItemTest() {
        populateData();
        Collection<Comment> comments = commentStorage.findByItemIsOrderByCreatedDesc(testItem);
        Assertions.assertTrue(comments.stream().allMatch(
                (Comment comment) -> comment.getItem().getId().equals(testItem.getId())));
        Assertions.assertEquals(3, comments.size());
    }
}
