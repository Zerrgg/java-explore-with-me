package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.util.List;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationRequest {
    @Size(min = MIN_LENGTH_TITLE, max = MAX_LENGTH_TITLE_COMPILATION)
    String title;

    Boolean pinned;
    List<Long> events;
}