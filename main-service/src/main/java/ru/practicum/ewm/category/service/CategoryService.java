package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto add(NewCategoryDto newCategoryDto);

    CategoryDto update(Long id, NewCategoryDto categoryDto);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Pageable pageable);

    void deleteById(Long catId);
}
