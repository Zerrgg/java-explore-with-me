package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping(path = "/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Collection<CommentDto> getAllComments(@PathVariable Long eventId) {
        return commentService.getAllCommentsFromEvent(eventId);
    }

    @GetMapping(path = "/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable Long commentId) {
        return commentService.getComment(commentId);
    }
}
