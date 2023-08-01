package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);
        Category category = categoryMapper.newCategoryDtoToCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Pageable pageable) {
        log.info("Получение всего списка категорий");
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        log.info("Получение категории по id = {}", catId);
        Category category = checkCategory(catId);
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(Long catId, NewCategoryDto categoryDto) {
        log.info("Изменение категории {} с id = {}", categoryDto, catId);
        Category category = checkCategory(catId);
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long catId) {
        log.info("Удаление категории по id = {}", catId);
        checkCategory(catId);
        List<Event> eventList = eventRepository.findAllByCategoryId(catId);
        if (eventList.isEmpty()) {
            log.info("Категория по id = {} удалена.", catId);
            categoryRepository.deleteById(catId);
        } else {
            log.info("Категория не может быть удалена, так как есть события, связанные с ней.");
            throw new ConflictException("Категория не может быть удалена, так как есть события, связанные с ней.");
        }
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> {
                            log.warn("Категории с таким id = {} не существует.", catId);
                            return new NotFoundException(String.format("Категории с таким id = %d не существует.", catId));
                        }
                );

    }

}