package ru.practicum.handler;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;

public interface UserActionHandler {
    List<EventSimilarityAvro> calculateSimilarity(UserActionAvro userActionAvro);

}
