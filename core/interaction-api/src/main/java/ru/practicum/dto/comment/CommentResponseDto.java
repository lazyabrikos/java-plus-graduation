package ru.practicum.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDto {
    private Long id;
    private Long eventId;
    private String authorName;
    private String text;
    private LocalDateTime createdAt;
}
