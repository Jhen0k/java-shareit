package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.dto.BookingOwnerByItem;
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
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemValidation itemValidation;
    @Mock
    private UserValidation userValidation;
    @Mock
    private UserMapper userMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentListMapper commentListMapper;
    @Mock
    private BookingMapper bookingMapper;

    @Test
    void createItem() {
        int userId = 1;
        int requestId = 1;
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        UserDto userDto = UserDto.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        ItemDto itemDto = new ItemDto(1, userDto, "name", "description", true, requestId, new ArrayList<>());

        doNothing().when(itemValidation).checkValidateItem(itemDto);
        doNothing().when(userValidation).checkUser(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toEntity(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto responseItem = itemService.createItem(itemDto, userId);

        assertThat(itemDto, equalTo(responseItem));

        verify(itemValidation, times(1)).checkValidateItem(itemDto);
        verifyNoMoreInteractions(itemValidation);

        verify(userValidation, times(1)).checkUser(userId);
        verifyNoMoreInteractions(userValidation);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userMapper);

        verify(itemMapper, times(1)).toEntity(itemDto);
        verifyNoMoreInteractions(itemMapper);

        verify(itemRepository, times(1)).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemMapper, times(1)).toDto(item);
        verifyNoMoreInteractions(itemMapper);
    }

    @Test
    void updateItem() {
        int userId = 1;
        int itemId = 1;
        int requestId = 1;
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        UserDto userDto = UserDto.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        ItemDto itemDto = new ItemDto(1, userDto, "name", "description", true, requestId, new ArrayList<>());

        doNothing().when(itemValidation).checkItemByUser(userId, itemId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(itemMapper.toEntity(itemDto)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());
        when(commentListMapper.toListDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        ItemDto responseItem = itemService.updateItem(itemDto, userId, itemId);

        assertThat(itemDto, equalTo(responseItem));

        verify(itemValidation, times(1)).checkItemByUser(userId, itemId);
        verifyNoMoreInteractions(itemValidation);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);

        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userMapper);

        verify(itemMapper, times(1)).toEntity(itemDto);
        verifyNoMoreInteractions(itemMapper);

        verify(itemRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(itemRepository);

        verify(itemRepository, times(1)).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemMapper, times(1)).toDto(item);
        verifyNoMoreInteractions(itemMapper);

        verify(commentRepository, times(1)).findAllByItemId(itemId);
        verifyNoMoreInteractions(commentRepository);

        verify(commentListMapper, times(1)).toListDto(new ArrayList<>());
        verifyNoMoreInteractions(commentListMapper);
    }

    @Test
    void findItemWithOneParam() {
        int itemId = 1;
        User user = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        BookingOwnerByItem bookingOwnerByItem = new BookingOwnerByItem(1, "2023-12-13T18:00:00", "2023-12-13T18:00:00", 1);
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(1, new UserDto(), "name",
                "description", true, new ItemRequest(), new ArrayList<>(), bookingOwnerByItem, bookingOwnerByItem);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemWithBookingDto(item)).thenReturn(itemWithBookingsDto);
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());
        when(commentListMapper.toListDto(new ArrayList<>())).thenReturn(new ArrayList<>());

        ItemWithBookingsDto responseItemWithBookingDto = itemService.findItem(itemId);

        assertThat(responseItemWithBookingDto, equalTo(itemWithBookingsDto));

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);

        verify(itemMapper, times(1)).toItemWithBookingDto(item);
        verifyNoMoreInteractions(itemMapper);

        verify(commentRepository, times(1)).findAllByItemId(itemId);
        verifyNoMoreInteractions(commentRepository);

        verify(commentListMapper, times(1)).toListDto(new ArrayList<>());
        verifyNoMoreInteractions(commentListMapper);
    }

    @Test
    void findItemWithTwoParam() {
        int userId = 1;
        int itemId = 1;
        int bookingId = 1;
        User user = User.builder().id(userId).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, user, "name", "description", true);
        Booking booking1 = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(4), user, item, StatusBooking.WAITING);
        BookingOwnerByItem bookingOwnerByItem = new BookingOwnerByItem(1, "2023-12-13T18:00:00", "2023-12-13T18:00:00", 1);
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(1, new UserDto(), "name",
                "description", true, new ItemRequest(), new ArrayList<>(), bookingOwnerByItem, bookingOwnerByItem);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemValidation.checkItem(Optional.of(item))).thenReturn(Optional.of(item));
        when(itemMapper.toItemWithBookingDto(item)).thenReturn(itemWithBookingsDto);
        //when(bookingRepository.findBookingsByItem(item)).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemId(itemId)).thenReturn(new ArrayList<>());
        when(commentListMapper.toListDto(new ArrayList<>())).thenReturn(new ArrayList<>());
        when(bookingRepository.findBookingsByItem(item)).thenReturn(List.of(booking1, booking1));
        when(bookingMapper.toBookingOwnerByItem(booking1)).thenReturn(bookingOwnerByItem);

        ItemWithBookingsDto responseItemWithBookingDto = itemService.findItem(itemId, userId);

        assertThat(itemWithBookingsDto, equalTo(responseItemWithBookingDto));

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);

        verify(itemValidation, times(1)).checkItem(Optional.of(item));
        verifyNoMoreInteractions(itemValidation);

        verify(itemMapper, times(1)).toItemWithBookingDto(item);
        verifyNoMoreInteractions(itemMapper);

        verify(bookingRepository, times(1)).findBookingsByItem(item);
        verifyNoMoreInteractions(bookingRepository);

        verify(commentRepository, times(1)).findAllByItemId(itemId);
        verifyNoMoreInteractions(commentRepository);

        verify(commentListMapper, times(1)).toListDto(new ArrayList<>());
        verifyNoMoreInteractions(commentListMapper);
    }

    @Test
    void findAllItemForOwner() {
        int userId = 1;
        int from = 0;
        int size = 10;
        Pageable pageable = Paginator.getPageable(from, size);
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        Page<Item> itemPage = new PageImpl<>(itemList, PageRequest.of(0, 10), itemList.size());
        BookingOwnerByItem bookingOwnerByItem = new BookingOwnerByItem(1, "2023-12-13T18:00:00", "2023-12-13T18:00:00", 1);
        ItemWithBookingsDto itemWithBookingsDto = new ItemWithBookingsDto(1, new UserDto(), "name",
                "description", true, new ItemRequest(), new ArrayList<>(), bookingOwnerByItem, bookingOwnerByItem);

        doNothing().when(userValidation).checkUser(userId);
        when(itemRepository.findItemByOwnerId(userId, pageable)).thenReturn(itemPage);
        when(itemMapper.toItemWithBookingDto(any(Item.class))).thenReturn(itemWithBookingsDto);

        List<ItemWithBookingsDto> response = itemService.findAllItemForOwner(userId, from, size);

        assertThat(itemList.size(), equalTo(response.size()));

        verify(userValidation, times(1)).checkUser(userId);
        verifyNoMoreInteractions(userValidation);

        verify(itemRepository, times(1)).findItemByOwnerId(userId, pageable);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void searchAvailableItem() {
        int from = 0;
        int size = 10;
        Pageable pageable = Paginator.getPageable(from, size);
        String text = "text";
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item());
        itemList.add(new Item());
        Page<Item> itemPage = new PageImpl<>(itemList, PageRequest.of(0, 10), itemList.size());

        when(itemRepository.searchByNameAndDescriptionAndAvailable(text, pageable)).thenReturn(itemPage);
        when(itemMapper.toDto(any(Item.class))).thenReturn(new ItemDto());

        List<ItemDto> response = itemService.searchAvailableItem(text, from, size);

        assertThat(itemList.size(), equalTo(response.size()));

        verify(itemRepository, times(1)).searchByNameAndDescriptionAndAvailable(text, pageable);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void findItemForRequest() {
        int requestId = 1;
        Item item = new Item(2, "name", "description", true);
        List<Item> items = List.of(item);

        when(itemRepository.findAllByItemRequest_Id(requestId)).thenReturn(items);
        when(itemMapper.toItemForRequest(item)).thenReturn(new ItemForRequest());

        List<ItemForRequest> response = itemService.findItemForRequest(requestId);

        assertNotNull(response);
        assertEquals(1, response.size());

        verify(itemRepository, times(1)).findAllByItemRequest_Id(requestId);
        verifyNoMoreInteractions(itemRepository);

        verify(itemMapper, times(1)).toItemForRequest(item);
        verifyNoMoreInteractions(itemMapper);
    }
}
