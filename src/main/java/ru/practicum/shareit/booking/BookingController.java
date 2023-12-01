package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingRequestDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") int userId) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateStatusBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                                  @PathVariable("bookingId") int bookingId,
                                                  @RequestParam Boolean approved) {
        return bookingService.updateStatusBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto findBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> findBookingsByUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                       @RequestParam(value = "state", defaultValue = "ALL")
                                                       String state) {
        return bookingService.findBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> findAllBookingsByItemsOwner(@RequestHeader("X-Sharer-User-Id") int userId,
                                                                @RequestParam(value = "state", defaultValue = "ALL")
                                                                String state) {
        return bookingService.findAllBookingsByItemsOwner(userId, state);
    }

}
