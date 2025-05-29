package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserShortDto mapUserToUserShortDto(User user);

    User mapNewUserToUser(NewUserRequest newUser);

    UserDto mapUserToUserDto(User user);

    List<UserDto> mapUsersToUsersDto(List<User> users);
}
