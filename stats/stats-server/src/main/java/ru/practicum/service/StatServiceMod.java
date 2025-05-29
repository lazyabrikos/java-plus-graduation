package ru.practicum.service;


import ru.practicum.dto.stats.HitRequestDto;
import ru.practicum.dto.stats.StatsResponseDto;

import java.util.List;

public interface StatServiceMod {
    StatsResponseDto save(HitRequestDto body);

    List<StatsResponseDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
