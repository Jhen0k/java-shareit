package ru.practicum.shareit.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.CommentMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidation;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentValidation commentValidation;
    @Mock
    private ItemValidation itemValidation;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserValidation userValidation;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;

    private final LocalDateTime endLocalDataTime = LocalDateTime.of(2024, 12, 13, 21, 12, 10);

    @Test
    @DisplayName("Создать комментарий")
    void createComment() {
        int userId = 1;
        int itemId = 1;
        int bookingId = 1;
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Comment comment = new Comment(1, "text", item, user, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(1, "text", "name", endLocalDataTime);
        Booking booking1 = new Booking(bookingId, null, LocalDateTime.MIN, null, null, StatusBooking.APPROVED);
        List<Booking> bookings = List.of(booking1);


        doNothing().when(commentValidation).checkValidateComment(commentDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemValidation.checkItem(Optional.of(item))).thenReturn(Optional.of(item));
        doNothing().when(userValidation).checkUser(userId);
        when(bookingRepository.findBookingByItemIdAndBookerId(itemId, userId)).thenReturn(bookings);
        when(commentMapper.toEntity(commentDto, user, item)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto commentDto1 = commentService.createComment(commentDto, userId, itemId);

        assertEquals(commentDto1.getId(), commentDto.getId());
        booking1.setStatusBooking(StatusBooking.REJECTED);
        assertThrows(ValidationException.class, () -> commentService.createComment(commentDto, userId, itemId));
    }
}
