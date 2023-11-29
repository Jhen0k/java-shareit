package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mappers.ItemMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

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
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        checkValidateItem(itemDto);
        userService.findUser(userId);
        itemDto.setOwnerId(userId);
        Item item = itemMapper.toEntity(itemDto);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int userId, int itemId) {
        userService.findUser(userId);
        checkItemByUser(userId, itemId);
        itemDto.setOwnerId(userId);
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
        /*itemDtoUpdate.setComments(commentListMapper
                .toListDto(commentRepository.findAllByItemId(itemOld.getId())));*/

        return itemDtoUpdate;
    }

    @Override
    public ItemDto findItem(int itemId) {
        return itemMapper.toDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> findAllItemForOwner(int userId) {
        Optional.of(userRepository.findById(userId)).get().orElseThrow();
        List<Item> items = itemRepository.findItemByOwnerId(userId);

        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        return itemRepository.searchByNameAndDescriptionAndAvailable(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private void checkItemByUser(int userId, int itemId) {
        if (!itemRepository.existsItemByIdAndOwnerId(itemId, userId)) {
            throw new ItemNotFoundException("Запрашиваемая вещь отсутствует у данного пользователя");
        }
    }

    private void checkValidateItem(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Предмета вещи не может быть пустым");
        } else if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Описание предмета не может быть пустым");
        } else if (item.getAvailable() == null) {
            log.error("Ошибка добавления предмета.");
            throw new ValidationException("Необходимо выбрать статус для предмета");
        }
    }
}
