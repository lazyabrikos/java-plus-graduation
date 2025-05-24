package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.clients.UserClient;
import ru.practicum.clients.event.AdminEventClient;
import ru.practicum.dto.comment.CommentRequestDto;
import ru.practicum.dto.comment.CommentResponseDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.user.UserDto;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;
import ru.practicum.errors.exceptions.DataConflictException;
import ru.practicum.errors.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AdminEventClient eventClient;
    private final UserClient userClient;

    @Override
    public CommentResponseDto createComment(Long eventId, Long userId, CommentRequestDto commentRequestDto) {

        UserDto userDto = userClient.getById(userId);

        Comment comment = commentMapper.toComment(commentRequestDto, userDto.getId(), eventId, LocalDateTime.now());
        log.info("Comment = {}", comment);

        log.info("Comment for creation = {}", comment);

        return commentMapper.toDto(commentRepository.save(comment), userDto.getName());
    }

    @Override
    public List<CommentResponseDto> getCommentByTargetId(Long id) {
        List<Comment> comments = commentRepository.findAllByEventId(id);
        return commentMapper.toDtoList(comments);
    }

    @Override
    public CommentResponseDto getCommentByCommentId(Long eventId, Long id) {
        EventFullDto event = eventClient.findById(eventId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No comment with id = " + id));
        return commentMapper.toDto(comment, "");
    }

    @Override
    public List<CommentResponseDto> getUserComments(Long id) {
        UserDto user = userClient.getById(id);
        List<Comment> comments = commentRepository.findAllByAuthorId(id);
        return commentMapper.toDtoList(comments);
    }

    @Override
    public CommentResponseDto updateCommentAsAuthor(Long eventId, Long commentId, Long userId,
                                                    CommentRequestDto commentDto) {
        UserDto userDto = userClient.getById(userId);
        EventFullDto event = eventClient.findById(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("No comment with id =" + commentId));
        if (!comment.getAuthorId().equals(userId)) {
            throw new DataConflictException("Not user cannot update comment");
        }
        comment.setText(commentDto.getText());
        return commentMapper.toDto(commentRepository.save(comment), userDto.getName());
    }

    @Override
    public CommentResponseDto updateCommentAsAdmin(Long eventId, Long commentId, CommentRequestDto commentDto) {
        EventFullDto event = eventClient.findById(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("No comment with id =" + commentId));
        comment.setText(commentDto.getText());
        return commentMapper.toDto(commentRepository.save(comment), "");

    }

    @Override
    public void deleteCommentAsAuthor(Long eventId, Long userId, Long commentId) {
        EventFullDto event = eventClient.findById(eventId);
        log.info("Event = {}", event);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("No comment with id =" + commentId));
        log.info("Comment = {}", comment);
        if (!comment.getAuthorId().equals(userId)) {
            throw new DataConflictException("Not user cannot delete comment");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentAsAdmin(Long eventId, Long commentId) {
        EventFullDto event = eventClient.findById(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("No comment with id =" + commentId));
        commentRepository.deleteById(commentId);
    }
}
