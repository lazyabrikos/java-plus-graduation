package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.categories.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventShortDto> getAllEventOfUser(Long userId, Integer from, Integer size);

    EventLongDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventOfUserById(Long userId, Long eventId);

    EventLongDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestEventStatus(Long userId, Long eventId,
                                                            EventRequestStatusUpdateRequest request);

    List<EventLongDto> getAllEventsByAdmin(EventAdminParams eventAdminParams);

    EventLongDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);


    List<EventShortDto> getPublicEvents(EventPublicParams param);

    Event getEventById(Long eventId);

    List<Event> getAllEventsByListId(List<Long> eventsId);

    Optional<Event> findByCategory(Category category);

    EventFullDto getEventDtoByIdWithHit(Long id, HttpServletRequest httpServletRequest);
}
