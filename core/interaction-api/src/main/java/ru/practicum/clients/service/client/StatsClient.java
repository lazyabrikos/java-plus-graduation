package ru.practicum.clients.service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.HitRequestDto;
import ru.practicum.StatsResponseDto;
import ru.practicum.errors.exceptions.StatsServerUnavailable;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class StatsClient {

    private final DiscoveryClient discoveryClient;
    @Value("${statsServiceId}")
    private String statsServiceId;

    @Autowired
    public StatsClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(statsServiceId)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + statsServiceId
            );
        }
    }
    public List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ServiceInstance instance = getInstance();

        String uriWithParams = UriComponentsBuilder.newInstance()
                .uri(URI.create("http://" + instance.getHost() + ":" + instance.getPort()))
                .path("/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("uris", uris)
                .queryParam("unique", unique)
                .toUriString();

        RestClient restClient = RestClient.builder().baseUrl(uriWithParams).build();


        try {
            var response = restClient.get()
                    .uri(uriWithParams)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StatsResponseDto>>() {
                    });
            return response;
        } catch (Exception exp) {
            return Collections.emptyList();
        }
    }

    public StatsResponseDto hit(HitRequestDto statDto) {
        ServiceInstance instance = getInstance();
        String uri = UriComponentsBuilder.newInstance()
                .uri(URI.create("http://" + instance.getHost() + ":" + instance.getPort()))
                .path("/hit")
                .toUriString();

        RestClient restClient = RestClient.builder().baseUrl(uri).build();

        try {
            return restClient.post().uri(uri).body(statDto).retrieve().body(StatsResponseDto.class);
        } catch (Exception exp) {
            log.info(exp.getMessage());
            return null;
        }
    }


}
