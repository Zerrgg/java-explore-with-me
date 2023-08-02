package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;


    @Override
    public CommentDto addComment(NewCommentDto newCommentDto, Long eventId, Long userId) {
        Event event = checkEvent(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Комментарии разрешены только к опубликованным событиям");
            throw new ConflictException("Комментарии разрешены только к опубликованным событиям");
        }

        Comment comment = Comment.builder()
                .text(newCommentDto.getText())
                .author(checkUser(userId))
                .event(event)
                .createdOn(LocalDateTime.now())
                .build();
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto getComment(Long commentId) {
        return CommentMapper.INSTANCE.toCommentDto(getCommentById(commentId));
    }

    @Override
    public List<CommentDto> getAllCommentsFromEvent(Long eventId) {
        checkEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId);
        return comments.stream()
                .map(CommentMapper.INSTANCE::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentById(commentId);
        checkUserIsOwner(comment.getAuthor().getId(), userId);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        checkUserIsOwner(comment.getAuthor().getId(), userId);
        checkCommentEditTime(comment);
        comment.setText(commentDto.getText());
        comment.setEditedOn(LocalDateTime.now());
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentAdmin(Long commentId, NewCommentDto commentDto) {
        Comment comment = getCommentById(commentId);
        comment.setText(commentDto.getText());
        return CommentMapper.INSTANCE.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void checkCommentEditTime(Comment comment) {
        LocalDateTime createdOn = comment.getCreatedOn();
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdOn, now);
        if (duration.toHours() > 24) {
            throw new ConflictException("Комментарий нельзя редактировать, по истечению суток");
        }
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с таким id не существует."));
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!id.equals(userId)) {
            log.warn("Пользователь не является владельцем.");
            throw new ConflictException("Пользователь не является владельцем.");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Не найден пользователь с id={}: ", userId);
            return new NotFoundException(String.format(
                    "Не найден пользователь с id=%d", userId));
        });
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
                    log.warn("Не найдено событие с id={}: ", eventId);
                    return new NotFoundException(String.format(
                            "Не найдено событие с id=%d", eventId));
                }
        );
    }
}