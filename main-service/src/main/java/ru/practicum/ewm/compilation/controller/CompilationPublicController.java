package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.dto.GlobalConstants.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.dto.GlobalConstants.PAGE_DEFAULT_SIZE;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(required = false, defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = PAGE_DEFAULT_SIZE) @Positive Integer size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getById(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}
