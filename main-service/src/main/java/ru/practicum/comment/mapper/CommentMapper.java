package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;


public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .lastUpdateTime(comment.getLastUpdateTime())
                .build();
    }

    public static Comment toComment(CommentDto commentDto, Event event, User author) {
        return Comment.builder()
                .id(commentDto.getId())
                .event(event)
                .author(author)
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .lastUpdateTime(commentDto.getLastUpdateTime())
                .build();
    }
}
