package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Pageable pageable);

    void deleteById(Long id);
}
