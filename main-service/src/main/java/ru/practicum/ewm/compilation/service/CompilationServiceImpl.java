package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final EventService eventService;
    private final CompilationRepository compilationRepository;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.info("Создание новой подборки событий с параметрами {}", newCompilationDto);

        List<Event> events = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            events = eventService.getEventsByIds(newCompilationDto.getEvents());
            checkSize(events, newCompilationDto.getEvents());
        }

        Compilation compilation = compilationRepository.save(CompilationMapper.INSTANCE.toCompilation(newCompilationDto, events));

        return getCompilationById(compilation.getId());
    }

    @Override
    public CompilationDto patch(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Обновление подборки событий с id {} и новыми параметрами {}", compId, updateCompilationRequest);

        Compilation compilation = checkCompilation(compId);

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());

            checkSize(events, updateCompilationRequest.getEvents());

            compilation.setEvents(events);
        }

        compilationRepository.save(compilation);

        return getCompilationById(compId);
    }

    @Override
    public void deleteById(Long compId) {
        log.info("Удаление подборки событий с id {}", compId);

        getCompilationById(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.info("Получение подборки событий по параметрам: pinned {}, from {}, size {}", pinned, from, size);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageRequest).getContent();
        } else {
            compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
        }
        log.info("Получена подборка событий по параметрам: pinned {}, from {}, size {}", pinned, from, size);

        return compilations.stream()
                .map(CompilationMapper.INSTANCE::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Получение подборки событий с id = {}", compId);
        Compilation compilation = checkCompilation(compId);
        log.info("Получена подборка событий с id = {}", compId);
        return CompilationMapper.INSTANCE.toCompilationDto(compilation);
    }

    private void checkSize(List<Event> events, List<Long> eventsIdToUpdate) {
        if (events.size() != eventsIdToUpdate.size()) {
            throw new NotFoundException("Некоторые события не найдены.");
        }
    }

    private Compilation checkCompilation(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() -> {
                    log.warn("Подборка с id = {} не найдена.", compId);
                    return new NotFoundException(String.format("Подборка с id = %d не найдено.", compId));
                }
        );
    }
}
