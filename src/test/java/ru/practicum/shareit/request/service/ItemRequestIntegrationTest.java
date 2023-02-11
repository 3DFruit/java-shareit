package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestPostDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Collection;

@Transactional
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @Test
    void getAllRequestsTest() {
        UserDto user1dto = userService.createUser(new UserDto(null, "user1", "user1@mail.com"));
        ItemRequestDto request1 = itemRequestService.createRequest(user1dto.getId(), new ItemRequestPostDto("request1"));
        ItemRequestDto request2 = itemRequestService.createRequest(user1dto.getId(), new ItemRequestPostDto("request2"));
        ItemRequestDto request3 = itemRequestService.createRequest(user1dto.getId(), new ItemRequestPostDto("request3"));

        itemService.addItem(user1dto.getId(), new ItemDto(null,
                "item1",
                "description",
                true,
                request3.getId(),
                null,
                null,
                null));
        itemService.addItem(user1dto.getId(), new ItemDto(null,
                "item2",
                "description",
                true,
                request1.getId(),
                null,
                null,
                null));
        itemService.addItem(user1dto.getId(), new ItemDto(null,
                "item3",
                "description",
                true,
                request2.getId(),
                null,
                null,
                null));
        itemService.addItem(user1dto.getId(), new ItemDto(null,
                "item4",
                "description",
                true,
                request3.getId(),
                null,
                null,
                null));

        Collection<ItemRequestDto> requests = itemRequestService.getAllRequests(user1dto.getId(), 0, 20);

        assertThat(requests, Matchers.notNullValue());
        for (ItemRequestDto request : requests) {
            assertThat(request.getId(), notNullValue());
            assertThat(request.getDescription(), notNullValue());
            assertThat(request.getDescription(), containsStringIgnoringCase("request"));
            assertThat(request.getCreated(), notNullValue());
            assertThat(request.getItems(), notNullValue());
            assertThat(request.getItems().size(), greaterThanOrEqualTo(1));
            assertThat(request.getRequester(), notNullValue());
            assertThat(request.getRequester(), equalTo(1L));
        }
    }
}
