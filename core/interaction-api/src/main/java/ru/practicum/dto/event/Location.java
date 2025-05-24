package ru.practicum.dto.event;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Location {
    private Float lat;
    private Float lon;
}
