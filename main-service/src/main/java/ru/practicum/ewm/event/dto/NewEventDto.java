package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.location.dto.LocationDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = MIN_LENGTH_ANNOTATION, max = MAX_LENGTH_ANNOTATION)
    String annotation;

	@NotNull
    Long category;

    @NotBlank
    @Size(min = MIN_LENGTH_DESCRIPTION, max = MAX_LENGTH_DESCRIPTION)
    String description;

    @NotNull
    @JsonFormat(pattern = DT_FORMAT, shape = JsonFormat.Shape.STRING)
    LocalDateTime eventDate;

    @NotNull
    @Valid
    LocationDto location;

    Boolean paid = false;

    @PositiveOrZero
    Integer participantLimit = 0;

    Boolean requestModeration = true;

    @NotBlank
    @Size(min = MIN_LENGTH_TITLE, max = MAX_LENGTH_TITLE)
    String title;
}
