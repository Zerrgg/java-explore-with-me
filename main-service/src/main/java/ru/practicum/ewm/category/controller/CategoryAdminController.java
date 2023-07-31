package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("POST запрос на добавление новой категории {}", newCategoryDto);
        return categoryService.add(newCategoryDto);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto update(@PathVariable Long catId,
                              @Valid @RequestBody NewCategoryDto categoryDto) {
        log.info("PATCH запрос на изменение категории {} с id = {}", categoryDto, catId);
        return categoryService.update(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long catId) {
        log.info("DELETE запрос на удаление категории с id = {}", catId);
        categoryService.deleteById(catId);
    }
}
