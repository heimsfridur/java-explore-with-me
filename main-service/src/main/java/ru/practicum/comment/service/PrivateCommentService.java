package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

public interface PrivateCommentService {
    CommentDto create(int eventId, int userId, CommentDto commentDto);

    CommentDto update(int eventId, int userId, int commentId, CommentDto commentDto);

    void delete(int eventId, int commentId, int userId);
}
