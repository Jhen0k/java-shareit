package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentListMapperTest {
    @InjectMocks
    private CommentListMapperImpl commentListMapper;
    @Mock
    private CommentMapperImpl commentMapper;

    @Test
    void toListDto() {
        int id = 1;
        String text = "text";
        User author = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, author, "name", "description", true);
        Comment comment = new Comment(id, text, item, author, LocalDateTime.now());
        CommentDto commentDto = new CommentDto(id, text, "name", LocalDateTime.now());
        List<Comment> comments = List.of(comment);

        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        List<CommentDto> commentDtoList = commentListMapper.toListDto(comments);


        assertEquals(commentDtoList.size(), comments.size());
        assertEquals(commentDtoList.get(0), commentDto);
    }

    @Test
    void toListEntity() {
        int id = 1;
        String text = "text";
        CommentDto commentDto = new CommentDto(id, text, "name", LocalDateTime.now());
        List<CommentDto> commentsDto = List.of(commentDto);

        List<Comment> commentList = commentListMapper.toListEntity(commentsDto);

        assertEquals(commentList.size(), commentsDto.size());
        assertEquals(commentList.get(0).getId(), commentsDto.get(0).getId());
    }
}