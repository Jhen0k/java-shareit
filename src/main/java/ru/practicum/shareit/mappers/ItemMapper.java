package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentListMapper.class})
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "item.owner")
    ItemDto toDto(Item item);

    @Mapping(target = "owner", source = "itemDto.ownerId")
    Item toEntity(ItemDto itemDto);
}
