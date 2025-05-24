package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.comment.CommentRequestDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.model.Comment;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", source = "authorName")
    CommentResponseDto toDto(Comment comment, String authorName);

    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "createdAt", source = "createdAt")
    Comment toComment(CommentRequestDto commentRequestDto, Long authorId,
                      Long eventId, LocalDateTime createdAt);

    List<CommentResponseDto> toDtoList(List<Comment> comments);
}
