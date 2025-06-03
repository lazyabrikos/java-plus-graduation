package ru.practicum.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorRun implements CommandLineRunner {

    private final ru.practicum.service.AggregatorService aggregatorService;

    @Override
    public void run(String... args) {
        log.info("Запуск aggregatorService.");
        aggregatorService.run();
    }
}