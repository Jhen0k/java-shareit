package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentListMapper {

    List<CommentDto> toListDto(List<Comment> comments);

    List<Comment> toListEntity(List<CommentDto> commentDto);
}
