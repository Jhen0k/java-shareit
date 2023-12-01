package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.mappers.CommentListMapper;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentListMapper commentListMapper;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        checkValidateItem(itemDto);
        checkUser(userId);
        UserDto user = userMapper.toDto(userRepository.findById(userId).orElseThrow());
        itemDto.setOwner(user);
        Item item = itemMapper.toEntity(itemDto);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        checkItemByUser(userId, itemId);
        checkUser(userId);
        UserDto user = userMapper.toDto(userRepository.findById(userId).orElseThrow());
        itemDto.setOwner(user);
        Item itemUpdate = itemMapper.toEntity(itemDto);
        Item itemOld = Optional.of(itemRepository.findById(itemId)).get().orElseThrow();

        if (itemUpdate.getName() != null) {
            itemOld.setName(itemUpdate.getName());
        }
        if (itemUpdate.getDescription() != null) {
            itemOld.setDescription(itemUpdate.getDescription());
        }
        if (itemUpdate.getAvailable() != null) {
            itemOld.setAvailable(itemUpdate.getAvailable());
        }

        ItemDto itemDtoUpdate = itemMapper.toDto(itemRepository.save(itemOld));
        itemDtoUpdate.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemOld.getId())));

        return itemDtoUpdate;
    }

    @Override
    public ItemWithBookingsDto findItem(int itemId) {
        ItemWithBookingsDto itemWithBookingsDto =
                itemMapper.toItemWithBookingDto(itemRepository.findById(itemId).get());
        itemWithBookingsDto.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemId)));

        return itemWithBookingsDto;
    }

    @Override
    public ItemWithBookingsDto findItem(int itemId, int userId) {
        LocalDateTime now = LocalDateTime.now();
        Item item = checkItem(itemId).get();
        ItemWithBookingsDto itemWithBookingsDto = itemMapper.toItemWithBookingDto(item);

        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findBookingsByItem(item);
            if (!bookings.isEmpty()) {

                Booking lastBooking = bookings.stream()
                        .filter(b -> b.getStart().isBefore(now))
                        .max(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                Booking nextBooking = bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .filter(b -> b.getStatusBooking() == StatusBooking.APPROVED)
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null);

                if (lastBooking != null) {
                    itemWithBookingsDto.setLastBooking(bookingMapper.toBookingOwnerByItem(lastBooking));
                }
                if (nextBooking != null) {
                    itemWithBookingsDto.setNextBooking(bookingMapper.toBookingOwnerByItem(nextBooking));
                }
            }
        }
        itemWithBookingsDto.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemId)));


        return itemWithBookingsDto;
    }

    @Override
    public List<ItemDto> findAllItemForOwner(int userId) {
        Optional.of(userRepository.findById(userId)).get().orElseThrow();
        List<Item> items = itemRepository.findItemByOwnerId(userId);

        return items.stream()
                .map(itemMapper::toDto)
                .peek(itemDto -> itemDto.setComments(commentListMapper
                        .toListDto(commentRepository.findAllByItemId(itemDto.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        return itemRepository.searchByNameAndDescriptionAndAvailable(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }

    private Optional<Item> checkItem(int itemId) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            throw new ItemNotFoundException("Вещи с указанным Id не найдено.");
        }
        return itemOptional;
    }

    private void checkItemByUser(int userId, int itemId) {
        if (!itemRepository.existsItemByIdAndOwnerId(itemId, userId)) {
            throw new ItemNotFoundException("Запрашиваемая вещь отсутствует у данного пользователя.");
        }
    }

    private void checkValidateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Название предмета не может быть пустым");
        } else if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Описание предмета не может быть пустым");
        } else if (item.getAvailable() == null) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Необходимо выбрать статус для предмета");
        }
    }
}
