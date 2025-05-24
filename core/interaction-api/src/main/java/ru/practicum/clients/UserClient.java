package ru.practicum.clients;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

@FeignClient(name = "user-service", path = "/admin/users")
public interface UserClient {

    @GetMapping("/{userId}")
    UserDto getById(@PathVariable Long userId);

    @GetMapping
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(value = "from", defaultValue = "0") int from,
                                  @RequestParam(value = "size", defaultValue = "10") int size) throws FeignException;

    @PostMapping
    UserDto createUser(@Valid @RequestBody NewUserRequest userRequest) throws FeignException;

    @DeleteMapping("/{id}")
    void deleteUser(@PathVariable @Positive Long id) throws FeignException;
}
