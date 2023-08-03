package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping(path = "events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long eventId,
                                    @PathVariable Long userId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return commentService.addComment(newCommentDto, eventId, userId);
    }

    @DeleteMapping("events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId, @PathVariable Long eventId) {
        commentService.deleteComment(commentId, userId);
    }

    @PatchMapping(path = "comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@RequestBody @Valid NewCommentDto commentDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long commentId) {
        return commentService.updateComment(userId, commentId, commentDto);
    }
}
