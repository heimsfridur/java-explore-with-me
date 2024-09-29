package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.PrivateCommentService;

@RequiredArgsConstructor
@RestController
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable int eventId,
                             @RequestParam int userId,
                             @RequestBody @Valid CommentDto commentDto) {
        return privateCommentService.create(eventId, userId, commentDto);
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto update(@PathVariable int eventId,
                             @PathVariable int commentId,
                             @RequestParam int userId,
                             @RequestBody @Valid CommentDto commentDto) {
        return privateCommentService.update(eventId, commentId, userId, commentDto);
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int eventId,
                       @PathVariable int commentId,
                       @RequestParam int userId) {
        privateCommentService.delete(eventId, commentId, userId);
    }
}
