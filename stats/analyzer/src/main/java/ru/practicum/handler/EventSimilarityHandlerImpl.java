package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.EventSimilarityMapper;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventSimilarityHandlerImpl implements EventSimilarityHandler {
    private final EventSimilarityRepository eventSimilarityRepository;

    @Transactional
    @Override
    public void handle(EventSimilarityAvro eventSimilarity) {
        Long eventA = eventSimilarity.getEventA();
        Long eventB = eventSimilarity.getEventB();

        if (!eventSimilarityRepository.existsByEventAAndEventB(eventA, eventB)) {
            eventSimilarityRepository.save(EventSimilarityMapper.mapToEventSimilarity(eventSimilarity));
        } else {
            EventSimilarity oldEventSimilarity = eventSimilarityRepository.findByEventAAndEventB(eventA, eventB);
            oldEventSimilarity.setScore(eventSimilarity.getScore());
            oldEventSimilarity.setTimestamp(eventSimilarity.getTimestamp());
        }
    }
}