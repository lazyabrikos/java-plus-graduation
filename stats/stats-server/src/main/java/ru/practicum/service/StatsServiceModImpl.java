package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.stats.HitRequestDto;
import ru.practicum.dto.stats.StatsResponseDto;
import ru.practicum.errors.exceptions.InvalidRequestException;
import ru.practicum.mappers.StatsMapper;
import ru.practicum.model.Hit;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StatsServiceModImpl implements StatServiceMod {
    private final StatRepository repository;
    private final StatsMapper mapper;

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public StatsResponseDto save(HitRequestDto body) {
        LocalDateTime timestamp = getDateTime(body.getTimestamp());
        Hit hit = new Hit();
        hit.setApp(body.getApp());
        hit.setUri(body.getUri());
        hit.setTimestamp(timestamp);
        hit.setIp(body.getIp());

        return mapper.toDto(repository.save(hit));
    }

    @Override
    public List<StatsResponseDto> getStats(String pathStart,
                                           String pathEnd,
                                           List<String> uris,
                                           Boolean unique) {
        List<StatsResponseDto> hits;
        LocalDateTime start = getDateTime(pathStart);
        LocalDateTime end = getDateTime(pathEnd);
        if (start.isAfter(end)) {
            throw new InvalidRequestException("Start is after end");
        }
        log.info("Uris = {}", uris);
        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                hits = repository.getStatsByUriWithUniqueIps(uris, start, end);
            } else {
                hits = repository.getStatsByUri(uris, start, end);
            }
        } else {
            if (unique) {
                hits = repository.getStatsByTimeWithUniqueIps(start, end);
            } else {
                hits = repository.findAllTimestampBetweenStartAndEnd(start, end);
            }
        }

        return hits;
    }

    private LocalDateTime getDateTime(String date) {
        try {
            return LocalDateTime.parse(date, ISO_FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.parse(date, CUSTOM_FORMATTER);
        }
    }
}