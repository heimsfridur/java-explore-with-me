package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.PublicCommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getAll(@PathVariable int eventId,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "10") int size) {
        return publicCommentService.getAll(eventId, from, size);
    }
}
