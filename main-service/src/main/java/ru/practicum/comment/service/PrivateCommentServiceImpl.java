package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto create(int eventId, int userId, CommentDto commentDto) {
        Event event = getEventById(eventId);
        User user = getUserById(userId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("It is impossible to comment unpublished event.");
        }

        if (user.getId().equals(event.getInitiator().getId())) {
            throw new ConflictException("It is impossible to comment your own event.");
        }

        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, event, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(int eventId, int commentId, int userId, CommentDto commentDto) {
        validateEventById(eventId);
        validateUserById(userId);

        Comment oldComment = getCommentById(commentId);

        checkIfUserIsAuthor(oldComment, userId);

        LocalDateTime lastUpdateTime = LocalDateTime.now();
        oldComment.setLastUpdateTime(lastUpdateTime);
        oldComment.setText(commentDto.getText());

        return CommentMapper.toCommentDto(commentRepository.save(oldComment));
    }

    @Override
    public void delete(int eventId, int commentId, int userId) {
        validateUserById(userId);

        Comment comment = getCommentById(commentId);
        checkIfUserIsAuthor(comment, userId);

        commentRepository.deleteById(commentId);
    }

    private Comment getCommentById(int id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id %d is not found.", id)));
    }

    private void validateUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }

    private User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d is not found.", id)));
    }

    private void validateEventById(int id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Event with id %d is not found.", id));
        }
    }

    private Event getEventById(int id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %d is not found.", id)));
    }

    private void checkIfUserIsAuthor(Comment comment, int userId) {
        if (comment.getAuthor().getId() != userId) {
            throw new ConflictException("User is not the author of comment.");
        }
    }
}
