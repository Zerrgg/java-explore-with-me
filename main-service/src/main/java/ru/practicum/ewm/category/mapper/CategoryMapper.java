package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

@Component
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category newCategoryDtoToCategory(NewCategoryDto newCategoryDto);

    default CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
