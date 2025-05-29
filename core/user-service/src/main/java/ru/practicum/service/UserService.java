package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);

    UserDto createUser(NewUserRequest newUser);

    UserDto findUserById(Long id);

    List<UserDto> findUsersByIds(List<Long> ids);
}
