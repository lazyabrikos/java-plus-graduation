package ru.practicum.service;

import ru.practicum.dto.comment.CommentRequestDto;
import ru.practicum.dto.comment.CommentResponseDto;

import java.util.List;

public interface CommentService {

    CommentResponseDto createComment(Long eventId, Long userId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getCommentByTargetId(Long id);

    CommentResponseDto getCommentByCommentId(Long eventId, Long id);

    List<CommentResponseDto> getUserComments(Long id);

    CommentResponseDto updateCommentAsAuthor(Long eventId, Long id, Long userId, CommentRequestDto comment);

    CommentResponseDto updateCommentAsAdmin(Long eventId, Long commentId, CommentRequestDto comment);

    void deleteCommentAsAuthor(Long eventId, Long userId, Long id);

    void deleteCommentAsAdmin(Long eventId, Long id);
}
