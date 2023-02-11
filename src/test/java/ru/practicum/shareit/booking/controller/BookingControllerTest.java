package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingState;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final BookingDto bookingDto = new BookingDto(1L,
            new ItemDto(1L,
                    "item name",
                    "description",
                    true,
                    null,
                    null,
                    null,
                    null),
            LocalDateTime.of(2022, 3, 5, 11, 12),
            LocalDateTime.of(2022, 3, 5, 11, 13),
            new UserDto(2L, "booker", "booker@mail.com"),
            BookingStatus.WAITING);

    private final BookingPostDto bookingPostDto = new BookingPostDto(1L,
            1L,
            LocalDateTime.of(2023, 3, 5, 11, 12),
            LocalDateTime.of(2023, 3, 5, 11, 13));

    @Test
    void addBookingTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingPostDto.class)))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPostDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));

    }

    @Test
    void addBookingWrongDateTest() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingPostDto.class)))
                .thenReturn(bookingDto);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new BookingPostDto(
                                1L,
                                1L,
                                LocalDateTime.of(2023, 3, 5, 11, 12),
                                LocalDateTime.of(2022, 3, 5, 11, 13)
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(new BookingPostDto(
                                1L,
                                1L,
                                LocalDateTime.of(2022, 3, 5, 11, 12),
                                LocalDateTime.of(2023, 3, 5, 11, 13)
                        )))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void patchBookingTest() throws Exception {
        when(bookingService.patchBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);
        mvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingPostDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));

    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getBookingsOfUserTest() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings?state=ALL&from=0&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getBookingsOfUserWrongParamsTest() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings?state=no&from=0&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/bookings?state=ALL&from=-1&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/bookings?state=ALL&from=0&size=0")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingsOfUserItemsTest() throws Exception {
        when(bookingService.getBookingsOfUserItems(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].start",
                        is(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].end",
                        is(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)), String.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    void getBookingsOfUserItemsWrongParamsTest() throws Exception {
        when(bookingService.getBookingsOfUser(anyLong(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto));
        mvc.perform(get("/bookings/owner?state=no&from=0&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/bookings/owner?state=ALL&from=-1&size=20")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=0")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
