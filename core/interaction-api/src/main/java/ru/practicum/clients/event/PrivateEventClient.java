package ru.practicum.clients.event;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;

import java.util.List;

@FeignClient(name = "event-service", path = "/users/{userId}/events")
public interface PrivateEventClient {
    @PostMapping
    ResponseEntity<EventLongDto> createEvent(@PathVariable Long userId,
                                                    @Valid @RequestBody NewEventDto newEventDto) throws FeignException;

    @GetMapping
    ResponseEntity<List<EventShortDto>> getAllEventForUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) throws FeignException;

    @GetMapping("/{eventId}")
    ResponseEntity<EventFullDto> getEventForUserById(@PathVariable Long userId, @PathVariable Long eventId) throws FeignException;

    @PatchMapping(value = "/{eventId}")
    ResponseEntity<EventLongDto> updateEventByUser(
            @PathVariable Long userId, @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) throws FeignException;
}
