package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventPublicParams;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.enums.events.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.formatter.Formatter;
import ru.practicum.clients.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class PublicEventController {

    private final EventService eventService;
    private final StatsService statsService;
    private final String AuthHeaderKey = "X-EWM-USER-ID";


    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
            @Positive @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, Formatter.getFormatter())
                : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, Formatter.getFormatter())
                : LocalDateTime.now().plusYears(20);

        EventPublicParams eventPublicParams = EventPublicParams.builder()
                .state(EventState.PUBLISHED)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(start)
                .rangeEnd(end)
                .onlyAvailable(onlyAvailable)
                .from(from)
                .size(size)
                .sort(sort)
                .build();
        List<EventShortDto> events = eventService.getPublicEvents(eventPublicParams);
        log.info("Calling the GET request to /events endpoint");
        return ResponseEntity.status(HttpStatus.OK)
                .body(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventDtoById(@RequestHeader(AuthHeaderKey) long userId, @PathVariable Long id,
                                                        HttpServletRequest httpServletRequest) {
        log.info("Calling the GET request to /events/{} endpoint", id);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventDtoByIdWithHit(userId, id, httpServletRequest));
    }

    @GetMapping("/recommendations")
    public List<EventShortDto> getEventsRecommendations(@RequestHeader(AuthHeaderKey) long userId,
                                                        @RequestParam(defaultValue = "10") int maxResults) {
        return eventService.getEventsRecommendations(userId, maxResults);
    }

    @PutMapping("/{eventId}/like")
    public void addLikeToEvent(@PathVariable Long eventId, @RequestHeader(AuthHeaderKey) long userId) {
        eventService.addLikeToEvent(eventId, userId);
    }
}
