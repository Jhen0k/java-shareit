package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated());
    }

    public Comment toEntity(CommentDto commentDto, User user, Item item) {
        return new Comment(commentDto.getId(),
                commentDto.getText(),
                item,
                user,
                commentDto.getCreated());
    }
}
