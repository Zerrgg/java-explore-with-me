package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        log.info("Добавление нового пользователя {}", newUserRequest);
        User user = userMapper.toUser(newUserRequest);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, Pageable pageable) {
        log.info("Получение списка пользователей.");
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(pageable)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllByIdIn(ids, pageable)
                    .stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteById(Long id) {
        log.info("Удаление пользователя с id = {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Не найден пользователь с id = {} не найден", id);
                            return new NotFoundException(String.format("Не найден пользователь с id: %d", id));
                        }
                );
        userRepository.delete(user);
    }
}
