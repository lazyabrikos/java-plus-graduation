package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.clients.UserActionClient;
import ru.practicum.clients.UserClient;
import ru.practicum.clients.event.AdminEventClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.enums.events.EventState;
import ru.practicum.errors.exceptions.DataConflictException;
import ru.practicum.errors.exceptions.NotFoundException;
import ru.practicum.errors.exceptions.ValidationException;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.enums.requests.RequestStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    final AdminEventClient eventClient;
    private final RequestMapper requestMapper;
    private final UserClient userClient;
    private final RequestRepository requestRepository;
    private final UserActionClient userActionClient;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) throws DataConflictException, NotFoundException {
        UserDto user = userClient.getById(userId);
        log.info("Send request to event-client");
        EventFullDto event = eventClient.findById(eventId);

        if (event.getInitiator().equals(userId)) {
            throw new DataConflictException("Создатель события не может подать заявку на участие");
        }
        if (!event.getState().equals(  EventState.PUBLISHED.name())) {
            throw new DataConflictException("Событие не опубликовано");
        }
        List<Request> requests = getRequestsByEventId(event.getId());
        if (participationLimitIsFull(event)) {
            throw new DataConflictException("Превышен лимит заявок на участие в событии");
        }
        for (Request request : requests) {
            if (request.getRequesterId().equals(userId)) {
                throw new DataConflictException("Повторная заявка на участие в событии");
            }
        }

        Request newRequest = createNewRequest(userId, event);
        //userActionClient.collectUserAction(eventId, userId, ActionTypeProto.ACTION_REGISTER, Instant.now());
        return requestMapper.mapRequest(requestRepository.save(newRequest));
    }

    @Override
    public List<RequestDto> getUserRequests(Long userId) throws NotFoundException {
        userClient.getById(userId);
        return requestRepository.findByUserId(userId).stream()
                .map(requestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long userId, Long eventId) throws ValidationException, NotFoundException {
        List<Request> requests = getRequests(userId, eventId);
        log.info("Got requests = {}", requests.size());
        return requests.stream()
                .map(requestMapper::mapRequest)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto updateRequest(Long userId,
                                    Long eventId,
                                    EventRequestStatusUpdateRequest updateRequest) throws DataConflictException, ValidationException, NotFoundException {
        log.info("Send request to event client in update");
        EventFullDto event = eventClient.findById(eventId);
        List<Request> requests = getRequestsByEventId(eventId);
        long confirmedRequestsCounter = requests.stream().filter(r -> r.getStatus().equals(CONFIRMED_REQUEST)).count();

        List<RequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDto> rejectedRequests = new ArrayList<>();

        List<Request> result = new ArrayList<>();

        List<Request> pending = requests.stream()
                .filter(p -> p.getStatus().equals(PENDING_REQUEST)).collect(Collectors.toList());


        for (Request request : requests) {
            if (request.getStatus().equals(CONFIRMED_REQUEST) ||
                    request.getStatus().equals(REJECTED_REQUEST) ||
                    request.getStatus().equals(PENDING_REQUEST)) {

                if (updateRequest.getStatus().equals(CONFIRMED_REQUEST) && event.getParticipantLimit() != 0) {
                    if (event.getParticipantLimit() < confirmedRequestsCounter) {

                        pending.stream().peek(p -> p.setStatus(REJECTED_REQUEST)).toList();

                        throw new DataConflictException("Превышено число возможных заявок на участие");
                    }
                }

                if (updateRequest.getStatus().equals(REJECTED_REQUEST) && request.getStatus().equals(CONFIRMED_REQUEST)) {
                    throw new DataConflictException("Нельзя отменить подтверждённую заявку");
                }

                request.setStatus(updateRequest.getStatus());
                RequestDto participationRequestDto = requestMapper.mapRequest(request);

                if ("CONFIRMED".equals(participationRequestDto.getStatus())) {
                    confirmedRequests.add(participationRequestDto);
                } else if ("REJECTED".equals(participationRequestDto.getStatus())) {
                    rejectedRequests.add(participationRequestDto);
                }

                result.add(request);
                confirmedRequestsCounter++;

            } else {
                throw new ValidationException("Неверный статус заявки");
            }
        }

        event.setConfirmedRequests(confirmedRequestsCounter);
        log.info("Send request for saving event");
        eventClient.saveEvent(event);
        requestRepository.saveAll(pending);

        requestRepository.saveAll(result);

        return requestMapper.mapRequestWithConfirmedAndRejected(confirmedRequests, rejectedRequests);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) throws NotFoundException, ValidationException {

       userClient.getById(userId);
        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос не существует")
        );
        if (!request.getRequesterId().equals(userId)) {
            throw new ValidationException("Создатель заявки не userId=" + userId);
        }
        request.setStatus(CANCELED_REQUEST);
        return requestMapper.mapRequest(requestRepository.save(request));
    }

    @Override
    public boolean checkExistsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, String status) {
        return requestRepository.existsByEventIdAndRequesterIdAndStatus(eventId, userId, status);
    }

    private Request createNewRequest(Long userId, EventFullDto event) {
        Request newRequest = new Request();
        newRequest.setRequesterId(userId);
        newRequest.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0) {
            newRequest.setStatus(CONFIRMED_REQUEST);
        } else {
            newRequest.setStatus(PENDING_REQUEST);
        }
        newRequest.setEventId(event.getId());
        if (!event.getRequestModeration()) {
            newRequest.setStatus(ACCEPTED_REQUEST);
        }
        return newRequest;
    }

    private boolean participationLimitIsFull(EventFullDto event) throws DataConflictException {
        Long confirmedRequestsCounter = requestRepository.countByEventAndStatuses(event.getId(), List.of("CONFIRMED", "ACCEPTED"));
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmedRequestsCounter) {
            throw new DataConflictException("Превышено число заявок на участие");
        }
        return false;
    }

    private List<Request> getRequests(Long userId, Long eventId) throws ValidationException, NotFoundException {
        UserDto userDto = userClient.getById(userId);
        log.info("Got userDto = {}", userDto);
        EventFullDto event = eventClient.findById(eventId);
        log.info("Got event = {}", event);
        if (!userDto.getId().equals(event.getInitiator())) {
            throw new ValidationException("Пользователь не инициатор события c id=" + eventId);
        }
        return requestRepository.findByEventId(eventId);
    }

    private List<Request> getRequestsByEventId(Long eventId) throws NotFoundException {
        EventFullDto eventFullDto = eventClient.findById(eventId);
        return requestRepository.findByEventId(eventId);
    }
}