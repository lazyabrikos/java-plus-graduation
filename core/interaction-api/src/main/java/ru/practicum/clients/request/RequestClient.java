package ru.practicum.clients.request;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
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
                                    @RequestParam String status) throws ValidationException, DataConflictException,
                                                                            NotFoundException, FeignException;


    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) throws ValidationException, NotFoundException,
                                                                                              FeignException;
    @GetMapping("/{eventId}/check-user/{userId}")
    public boolean checkExistStatusRequest(@PathVariable Long eventId,@PathVariable Long userId,
                                    @RequestParam String status);
}
