package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventLongDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.enums.events.EventState;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    Event toEvent(NewEventDto newEventDto);

    @Mapping(source = "location.lat", target = "lat")
    @Mapping(source = "location.lon", target = "lon")
    @Mapping(target = "eventDate", expression = "java(parseDateTime(eventFullDto.getEventDate()))")
    @Mapping(target = "createdOn", expression = "java(parseDateTime(eventFullDto.getCreatedOn()))")
    @Mapping(target = "publishedOn", expression = "java(parseDateTime(eventFullDto.getPublishedOn()))")
    @Mapping(target = "state", expression = "java(parseEventState(eventFullDto.getState()))")
    Event fromEventFullDtoToEvent(EventFullDto eventFullDto);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "rating", expression = "java(rating)")
    EventShortDto toShortDto(Event event, Double rating);

    @Mapping(source = "event.lat", target = "location.lat")
    @Mapping(source = "event.lon", target = "location.lon")
    @Mapping(target = "rating", expression = "java(rating)")
    EventLongDto toLongDto(Event event, Double rating);

    @Mapping(source = "event.lat", target = "location.lat")
    @Mapping(source = "event.lon", target = "location.lon")
    EventLongDto toLongDto(Event event);

    @Mapping(target = "eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(source = "lat", target = "location.lat")
    @Mapping(source = "lon", target = "location.lon")
    EventFullDto toEventFullDto(Event event);

    default LocalDateTime parseDateTime(String dateTime) {
        if (dateTime == null) {
            return null;
        }
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    default EventState parseEventState(String state) {
        if (state == null) {
            return null;
        }
        return EventState.valueOf(state);
    }
}