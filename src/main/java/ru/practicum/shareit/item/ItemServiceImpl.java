package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequest;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.BookingMapper;
import ru.practicum.shareit.mappers.CommentListMapper;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.mappers.UserMapper;
import ru.practicum.shareit.paginator.Paginator;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserValidation;
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
    private final ItemValidation itemValidation;
    private final UserValidation userValidation;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final CommentListMapper commentListMapper;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        itemValidation.checkValidateItem(itemDto);
        userValidation.checkUser(userId);
        UserDto user = userMapper.toDto(userRepository.findById(userId).orElseThrow());
        itemDto.setOwner(user);
        Item item = itemMapper.toEntity(itemDto);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = new ItemRequest();
            itemRequest.setId(itemDto.getRequestId());
            item.setItemRequest(itemRequest);
        }
        ItemDto itemDto1 = itemMapper.toDto(itemRepository.save(item));
        if (itemDto.getRequestId() != null) {
            itemDto1.setRequestId(item.getItemRequest().getId());
        }

        return itemDto1;
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        itemValidation.checkItemByUser(userId, itemId);
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
                itemMapper.toItemWithBookingDto(itemRepository.findById(itemId).orElseThrow());
        itemWithBookingsDto.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemId)));

        return itemWithBookingsDto;
    }

    @Override
    public ItemWithBookingsDto findItem(int itemId, int userId) {
        boolean isOwner = false;
        Item item = itemValidation.checkItem(itemRepository.findById(itemId)).orElseThrow();
        if (item.getOwner().getId() == userId) {
            isOwner = true;
        }
        ItemWithBookingsDto itemWithBookingsDto = addLastAndNextBookingForItem(item, isOwner);

        itemWithBookingsDto.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemId)));
        return itemWithBookingsDto;
    }

    @Override
    public List<ItemWithBookingsDto> findAllItemForOwner(int userId, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size);
        userValidation.checkUser(userId);
        Page<Item> items = itemRepository.findItemByOwnerId(userId, pageable);

        return items.stream()
                .map(item -> addLastAndNextBookingForItem(item, true))
                .peek(itemDto -> itemDto.setComments(commentListMapper
                        .toListDto(commentRepository.findAllByItemId(itemDto.getId()))))
                .sorted(Comparator.comparing(ItemWithBookingsDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text, Integer from, Integer size) {
        Pageable pageable = Paginator.getPageable(from, size);
        return itemRepository.searchByNameAndDescriptionAndAvailable(text, pageable).stream()
                //.filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemForRequest> findItemForRequest(int requestId) {
        List<Item> items = itemRepository.findAllByItemRequest_Id(requestId);
        return items.stream()
                .map(item -> {
                    ItemForRequest itemForRequest = itemMapper.toItemForRequest(item);
                    Integer reqId = null;
                    if (item.getItemRequest() != null) {
                        reqId = item.getItemRequest().getId();
                    }
                itemForRequest.setRequestId(reqId);
                return itemForRequest;
                })
                .collect(Collectors.toList());
    }

    private ItemWithBookingsDto addLastAndNextBookingForItem(Item item, boolean isOwner) {
        LocalDateTime now = LocalDateTime.now();
        ItemWithBookingsDto itemWithBookingsDto = itemMapper.toItemWithBookingDto(item);

        if (isOwner) {
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
        return itemWithBookingsDto;
    }
}
