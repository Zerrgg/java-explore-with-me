package ru.practicum.ewm.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.ewm.dto.GlobalConstants.MAX_LENGTH_COMMENT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {
    @NotBlank
    @Size(max = MAX_LENGTH_COMMENT)
    String text;
}