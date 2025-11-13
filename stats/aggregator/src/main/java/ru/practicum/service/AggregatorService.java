package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.handler.UserActionHandler;

import java.time.Duration;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatorService implements Runnable {

    private final KafkaProducer producer;
    private final Consumer<Long, UserActionAvro> consumer;
    private final UserActionHandler handler;

    @Value("${aggregator.topic.user-action}")
    private String topicUserAction;
    @Value("${aggregator.topic.events-similarity}")
    private String topicEventSimilarity;
    @Value("${spring.kafka.consumer.poll-timeout}")
    private int pollTimeout;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(topicUserAction));

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));
                if (records.count() > 0) {
                    log.info("Получено " + records.count() + " сообщений.");
                }
                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro userActionAvro = record.value();
                    List<EventSimilarityAvro> result = handler.calculateSimilarity(userActionAvro);
                    log.info("Подготовлено " + result.size() + " сообщений.");
                    producer.send(result, topicEventSimilarity);
                    producer.flush();
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий.", e);
        } finally {
            try {
                producer.flush();
                consumer.commitAsync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}
