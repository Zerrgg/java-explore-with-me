package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.event.enums.AdminRequestState;
import ru.practicum.ewm.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    @Size(min = MIN_LENGTH_ANNOTATION, max = MAX_LENGTH_ANNOTATION)
    String annotation;

    Long categoryId;

    @Size(min = MIN_LENGTH_DESCRIPTION, max = MAX_LENGTH_DESCRIPTION)
    String description;

    @JsonFormat(pattern = DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @Valid
    LocationDto location;

    Boolean paid;

    @PositiveOrZero
    Integer participantLimit;

    Boolean requestModeration;

    AdminRequestState stateAction;

    @Size(min = MIN_LENGTH_TITLE, max = MAX_LENGTH_TITLE)
    String title;
}
