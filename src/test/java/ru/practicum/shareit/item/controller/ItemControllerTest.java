package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = new CommentDto(1L,
            "author",
            "comment",
            LocalDateTime.of(2022, 3, 5, 11, 12));

    private final ItemDto itemDto = new ItemDto(1L,
            "item name",
            "description",
            true,
            1L,
            null,
            null,
            List.of(commentDto));


    @Test
    void addItemTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));

    }

    @Test
    void addItemBlankNameTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new ItemDto(
                                null,
                                "    ",
                                "description",
                                true,
                                null,
                                null,
                                null,
                                null
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void addItemBlankDescriptionTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new ItemDto(
                                null,
                                "name",
                                "    ",
                                true,
                                null,
                                null,
                                null,
                                null
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void addItemNullAvailableTest() throws Exception {
        when(itemService.addItem(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new ItemDto(
                                null,
                                "name",
                                "description",
                                null,
                                null,
                                null,
                                null,
                                null
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    void addCommentTest() throws Exception {
        when(itemService.addComment(anyLong(), any(CommentPostDto.class), anyLong()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new CommentPostDto("comment")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.created",
                        is(commentDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)), String.class));

    }

    @Test
    void addCommentBlankTextTest() throws Exception {
        when(itemService.addComment(anyLong(), any(CommentPostDto.class), anyLong()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new CommentPostDto("   ")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemsTest() throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items?from=0&size=20").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking()), BookingDto.class))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking()), BookingDto.class))
                .andExpect(jsonPath("$[0].comments.*", hasSize(1)));
    }

    @Test
    void getItemsWrongParamsTest() throws Exception {
        when(itemService.getItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items?from=-1&size=20").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/items?from=0&size=0").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking", is(itemDto.getLastBooking()), BookingDto.class))
                .andExpect(jsonPath("$.nextBooking", is(itemDto.getNextBooking()), BookingDto.class))
                .andExpect(jsonPath("$.comments.*", hasSize(1)));
    }

    @Test
    void searchItemsTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=text&from=0&size=20").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemDto.getLastBooking()), BookingDto.class))
                .andExpect(jsonPath("$[0].nextBooking", is(itemDto.getNextBooking()), BookingDto.class))
                .andExpect(jsonPath("$[0].comments.*", hasSize(1)));
    }

    @Test
    void searchItemsWrongParamsTest() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));
        mvc.perform(get("/items/search?text=text&from=-1&size=20").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/items/search?text=text&from=0&size=0").header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchItem() throws Exception {
        when(itemService.patchItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
    }
}
