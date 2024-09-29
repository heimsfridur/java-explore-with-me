package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.service.AdminCommentService;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int commentId) {
        adminCommentService.delete(commentId);
    }
}
