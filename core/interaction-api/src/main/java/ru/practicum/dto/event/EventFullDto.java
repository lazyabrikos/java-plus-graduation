package ru.practicum.dto.event;

import lombok.*;
import ru.practicum.dto.category.CategoryDto;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private Long initiator;
    private Boolean paid;
    private Location location;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private String state;
    private String title;
    private Long confirmedRequests;
    private Double rating;
}