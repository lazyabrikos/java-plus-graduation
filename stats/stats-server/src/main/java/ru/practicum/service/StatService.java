package ru.practicum.service;

import ru.practicum.HitRequestDto;
import ru.practicum.StatsResponseDto;

import java.util.List;

public interface StatService {
    StatsResponseDto save(HitRequestDto body);

    List<StatsResponseDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
