package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto addComment(NewCommentDto newCommentDto, Long eventId, Long userId);

    CommentDto getComment(Long commentId);

    Collection<CommentDto> getAllCommentsFromEvent(Long eventId);

    void deleteComment(Long commentId, Long userId);

    CommentDto updateComment(Long userId, Long commentId, NewCommentDto commentDto);

    CommentDto updateCommentAdmin(Long commentId, NewCommentDto commentDto);

    void deleteCommentAdmin(Long comId);
}