package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

@Component
@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

}
