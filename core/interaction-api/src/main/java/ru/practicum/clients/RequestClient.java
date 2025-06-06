package ru.practicum.clients;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.errors.exceptions.DataConflictException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;

import java.util.List;

@FeignClient(name = "request-service", path = "/users/{userId}")
public interface RequestClient {

    @PostMapping("/requests")
    public RequestDto addEventRequest(@PathVariable Long userId,
                                      @RequestParam Long eventId) throws DataConflictException, NotFoundException,
                                                                                                FeignException;

    @GetMapping("/requests")
    public List<RequestDto> getUserRequests(@PathVariable Long userId) throws NotFoundException, FeignException;

    @GetMapping("/events/{eventId}/requests")
    public List<RequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) throws ValidationException,
                                                                                    NotFoundException, FeignException;

    @PatchMapping("/events/{eventId}/requests")
    public RequestDto updateRequest(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody RequestDto request) throws ValidationException, DataConflictException,
                                                                            NotFoundException, FeignException;


    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) throws ValidationException, NotFoundException,
                                                                                              FeignException;
    }
