package ru.practicum.ewm.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    String text;
    Long authorId;
    Long eventId;
    LocalDateTime createdOn;
    LocalDateTime editedOn;
}