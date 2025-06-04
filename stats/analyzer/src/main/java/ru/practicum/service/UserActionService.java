package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.handler.UserActionHandler;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionService implements Runnable {

    private final Consumer<Long, UserActionAvro> consumer;
    private final UserActionHandler userActionHandler;

    @Value("${analyzer.topic.user-action}")
    private String topicUserAction;

    @Value("${spring.kafka.consumer.poll-timeout}")
    private int pollTimeout;

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            consumer.subscribe(List.of(topicUserAction));
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(Duration.ofMillis(pollTimeout));

                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro action = record.value();
                    log.info("Получили действие пользователя {}", action);

                    userActionHandler.handle(action);
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topicUserAction);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}