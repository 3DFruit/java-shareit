package ru.practicum.shareit.item.utils;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentPostDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static Comment toComment(CommentPostDto dto) {
        return new Comment(dto.getText());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated());
    }
}
