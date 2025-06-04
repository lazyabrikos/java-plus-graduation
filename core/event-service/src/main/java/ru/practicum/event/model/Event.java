package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.categories.model.Category;
import ru.practicum.enums.events.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @Column(name = "initiator")
    private Long initiator;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "loc_lat")
    private Float lat;
    @Column(name = "loc_lon")
    private Float lon;
    @Column(name = "participant_limit")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    private String title;
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;
}