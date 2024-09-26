package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {
    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getAll(int eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Comment> comments = commentRepository.findAll(pageable).getContent();

        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }

}
