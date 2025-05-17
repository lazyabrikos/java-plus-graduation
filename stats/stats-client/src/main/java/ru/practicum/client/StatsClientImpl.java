package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClientImpl implements StatsClient {
    private final RestClient restClient;


    @Autowired
    public StatsClientImpl(@Value("${stats-server.url}") String statsUrl) {
        this.restClient = RestClient.create(statsUrl);
    }

    @Override
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            var response = restClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/stats")
                            .queryParam("start", start.format(formatter))
                            .queryParam("end", start.format(formatter))
                            .queryParam("uris", uris)
                            .queryParam("unique", unique)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatsResponseDto>>() {
                    });
            return response;
        } catch (Exception exp) {
            return Collections.emptyList();
        }
    }

    @Override
    public StatsResponseDto hit(HitRequestDto statDto) {
        try {
            return restClient.post().uri("/hit").body(statDto).retrieve().body(StatsResponseDto.class);
        } catch (Exception exp) {
            log.info(exp.getMessage());
            return null;
        }
    }


}
