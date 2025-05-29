package ru.practicum.clients.event;

import feign.FeignException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventLongDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.enums.events.EventState;

import java.util.List;

@FeignClient(name = "event-service", path = "/admin/events")
public interface AdminEventClient {
    @GetMapping
    List<EventLongDto> getAllEventsByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeEnd,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) throws FeignException;

    @PatchMapping(value = "/{eventId}")
    EventLongDto updateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest);

    @GetMapping("/{id}")
    EventFullDto findById(@PathVariable("id") @Positive Long id) throws FeignException;

    @PutMapping("/save")
    EventFullDto saveEvent(@RequestBody EventFullDto event) throws FeignException;
}
