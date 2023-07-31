package ru.practicum.ewm.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    Long id;
    String title;
    Boolean pinned;
    List<EventShortDto> events;
}
