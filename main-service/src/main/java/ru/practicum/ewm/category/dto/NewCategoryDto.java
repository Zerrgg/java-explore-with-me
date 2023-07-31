package ru.practicum.ewm.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import static ru.practicum.ewm.dto.GlobalConstants.MAX_LENGTH_CATEGORY;
import static ru.practicum.ewm.dto.GlobalConstants.MIN_LENGTH_CATEGORY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {
    @Size(min = MIN_LENGTH_CATEGORY, max = MAX_LENGTH_CATEGORY)
    @NotBlank
    String name;
}
