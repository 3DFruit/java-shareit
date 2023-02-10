package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import javax.persistence.EntityManager;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void updateItemTest() {
        UserDto user1dto = userService.createUser(new UserDto(null, "user1", "user1@mail.com"));
        UserDto updatedDto = new UserDto(null, "updated user", "updated@mail.com");
        userService.updateUser(user1dto.getId(), updatedDto);

        User user = em.createQuery("select u from User u where u.id = :id", User.class)
                .setParameter("id", user1dto.getId())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(updatedDto.getName()));
        assertThat(user.getEmail(), equalTo(updatedDto.getEmail()));
    }
}
