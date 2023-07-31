package ru.practicum.ewm.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewUserRequest {
    @Size(min = MIN_LENGTH_EMAIL, max = MAX_LENGTH_EMAIL)
    @Email
    @NotBlank
    String email;

    @Size(min = MIN_LENGTH_USER_NAME, max = MAX_LENGTH_USER_NAME)
    @NotBlank
    String name;
}
