package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.ewm.dto.GlobalConstants.DT_FORMAT;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequestDto {
    Long id;

    @JsonFormat(pattern = DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime created;

    Long event;
    Long requester;
    ParticipationRequestStatus status;
}
