package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHitDto {
    @NotBlank
    String app;

    @NotBlank
    String uri;

    @NotBlank
    String ip;

    @NotBlank
    String timestamp;
}
