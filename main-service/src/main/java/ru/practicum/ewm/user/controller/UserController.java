package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.ewm.dto.GlobalConstants.PAGE_DEFAULT_FROM;
import static ru.practicum.ewm.dto.GlobalConstants.PAGE_DEFAULT_SIZE;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.info("POST запрос на добавление нового пользователя {}", newUserRequest);
        return userService.add(newUserRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = PAGE_DEFAULT_FROM) @PositiveOrZero int from,
                                  @RequestParam(defaultValue = PAGE_DEFAULT_SIZE) @Positive int size) {
        log.info("GET запрос на получение списка пользователей.");
        return userService.getUsers(ids, PageRequest.of(from / size, size));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE запрос на удаление пользователя с id = {}", userId);
        userService.deleteById(userId);
    }
}
