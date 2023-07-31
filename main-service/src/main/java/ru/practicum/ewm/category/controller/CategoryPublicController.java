package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(
            @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
            @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {
        log.info("GET запрос на получение всех категорий.");
        return categoryService.getAll(PageRequest.of(from / size, size));
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable Long catId) {
        log.info("GET запрос на получение категории по id = {}", catId);
        return categoryService.getById(catId);
    }
}
