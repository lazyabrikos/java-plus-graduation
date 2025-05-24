package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.errors.exceptions.DataConflictException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.repository.UserRepository;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAllOrderById(size, from);
        } else {
            users = userRepository.findAllById(ids);
        }

        return userMapper.mapUsersToUsersDto(users);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with id =" + userId));
        userRepository.delete(user);
    }

    @Override
    public UserDto createUser(NewUserRequest newUser) {
        User user = userMapper.mapNewUserToUser(newUser);
        isEmailUnique(newUser.getEmail());
        return userMapper.mapUserToUserDto(userRepository.save(user));
    }

    private void isEmailUnique(String email) {
        boolean isEmailExists = userRepository.existsByEmail(email);
        if (isEmailExists) {
            throw new DataConflictException("User with this email already exists");
        }
    }

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with id =" + userId));
        return userMapper.mapUserToUserDto(user);
    }

    @Override
    public List<UserDto> findUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::mapUserToUserDto)
                .toList();

    }
}
