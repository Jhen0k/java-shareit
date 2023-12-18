package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentValidation commentValidation;
    private final ItemValidation itemValidation;
    private final UserValidation userValidation;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, int userId, int itemId) {
        commentValidation.checkValidateComment(commentDto);
        itemValidation.checkItem(itemRepository.findById(itemId));
        userValidation.checkUser(userId);

        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerId(itemId, userId);

        bookings.stream()
                .filter(b -> b.getStatusBooking() == StatusBooking.APPROVED &&
                        b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst().orElseThrow(() -> new ValidationException("Чтобы оставить комментарий необходимо " +
                        "наличие завершенного бронирования"));

        User user = userRepository.findById(userId).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        Comment comment = commentMapper.toEntity(commentDto, user, item);

        return commentMapper.toDto(commentRepository.save(comment));
    }
}
