package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentDto> getAll(int eventId, int from, int size);
}
