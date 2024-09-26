package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.exceptions.NotFoundException;

@Service
@AllArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    private final CommentRepository commentRepository;

    @Override
    public void delete(int commentId) {
        validateById(commentId);
        commentRepository.deleteById(commentId);
    }

    private void validateById(int id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException(String.format("Comment with id %d is not found.", id));
        }
    }

}
