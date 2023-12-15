package ru.practicum.shareit.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class CommentMapperTest {
    @InjectMocks
    private CommentMapperImpl commentMapper;

    @Test
    void toDto() {
        int id = 1;
        String text = "text";
        User author = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, author, "name", "description", true);
        LocalDateTime create = LocalDateTime.now();
        Comment comment = new Comment(id, text, item, author, create);


        CommentDto commentDto = commentMapper.toDto(comment);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(commentDto.getAuthorName(), comment.getAuthor().getName());
        assertNotNull(commentDto.getCreated());
    }

    @Test
    void toEntity() {
        int id = 1;
        String text = "text";
        String name = "name";
        User author = User.builder().id(1).name("name").email("mail@mail.ru").build();
        Item item = new Item(1, author, "name", "description", true);
        CommentDto commentDto = new CommentDto(id, text, name, LocalDateTime.now());

        Comment comment = commentMapper.toEntity(commentDto, author, item);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem(), item);
        assertEquals(comment.getAuthor(), author);
        assertNotNull(comment.getCreated());
    }
}
