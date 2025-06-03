package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.errors.exceptions.DataConflictException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class RequestController {

    final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addEventRequest(@PathVariable Long userId,
                                      @RequestParam Long eventId) throws DataConflictException, NotFoundException {
        return requestService.addRequest(userId, eventId);
    }

    @GetMapping("/requests")
    public List<RequestDto> getUserRequests(@PathVariable Long userId) throws NotFoundException {
        return requestService.getUserRequests(userId);
    }

    @RequestMapping(value = "/events/{eventId}/requests", method = RequestMethod.GET)
    public List<RequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) throws ValidationException, NotFoundException {
        log.info("Пришел Get запрос на получение всех запросов на участие в событие пользователя");
        return requestService.getRequestsByEventId(userId, eventId);
    }

    @RequestMapping(value = "/events/{eventId}/requests", method = RequestMethod.PATCH)
    public RequestDto updateRequest(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestParam String status) throws ValidationException, DataConflictException, NotFoundException {
        log.info("Got patch request with body = {}", status);
        return requestService.updateRequest(userId, eventId, status);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) throws ValidationException, NotFoundException {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{eventId}/check-user/{userId}")
    public boolean checkExistsByEventIdAndRequesterIdAndStatus(@PathVariable Long eventId,@PathVariable Long userId,
                                                               @RequestParam String status) {
        return requestService.checkExistsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }
}