package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.NotFoundBookingException;
import ru.practicum.shareit.booking.model.RequestBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.exception.NotFoundUserException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.HEADER_USER;

@FieldDefaults(level = AccessLevel.PRIVATE)
@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    static final String URL = "/bookings";

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService bookingService;

    BookingDto bookingDto;
    RequestBooking requestBooking;
    int userId;
    int bookingId;

    @BeforeEach
    public void setUp() {
        userId = 1;
        bookingId = 1;

        bookingDto = BookingDto.builder()
                .id(bookingId)
                .itemName("Отвертка")
                .bookerId(userId)
                .itemId(1)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        requestBooking = requestBooking.builder()
                .userId(1)
                .state("true")
                .size(10)
                .from(0)
                .build();
    }

    @Test
    @SneakyThrows
    public void addBookingWhenInvokedMethodReturnBooking() {
        when(bookingService.createBooking(bookingDto, userId)).thenReturn(bookingDto);

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void addBookingWhenInvokedMethodReturnStatusIsNotFound() {
        when(bookingService.createBooking(bookingDto, userId)).thenThrow(NotFoundUserException.class);

        mvc.perform(post(URL)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenInvokedMethodReturnBooking() {
        when(bookingService.removeBooking(bookingId, userId)).thenReturn(bookingDto);

        mvc.perform(delete(URL + "/{bookingId}", bookingId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void deleteBookingWhenBookingNotFoundReturnStatusIsNotFound() {
        when(bookingService.removeBooking(bookingId, userId)).thenThrow(NotFoundBookingException.class);

        mvc.perform(delete(URL + "/{bookingId}", bookingId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingByIdWhenInvokedMethodReturnBooking() {
        when(bookingService.getBookingById(bookingId, userId)).thenReturn(bookingDto);

        mvc.perform(get(URL + "/{bookingId}", bookingId)
                        .header(HEADER_USER, userId)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenInvokedMethodReturnBooking() {
        when(bookingService.setApprove(bookingId, true, userId)).thenReturn(bookingDto);

        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header(HEADER_USER, userId))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void setApproveWhenBookingNotFoundReturnStatusIsNotFound() {
        when(bookingService.setApprove(bookingId, true, userId)).thenThrow(NotFoundBookingException.class);

        mvc.perform(patch(URL + "/{bookingId}", bookingId)
                        .queryParam("approved", "true")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    public void getBookingForCurrentUserWhenInvokedMethodReturnOneBooking() {
        when(bookingService.getBookingByUser(requestBooking)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void getBookingForOwnerWhenInvokedMethodReturnOneBooking() {
        when(bookingService.getBookingForOwner(requestBooking)).thenReturn(List.of(bookingDto));

        mvc.perform(get(URL)
                        .queryParam("state", "ALL")
                        .queryParam("from", "0")
                        .queryParam("size", "10")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}
