package ru.practicum.handler;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.grpc.stats.event.ActionTypeProto;
import ru.practicum.ewm.grpc.stats.event.UserActionProto;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserActionHandlerImpl implements UserActionHandler {

    @Value("${collector.topic.user-action}")
    private String topic;

    private final Producer<Long, SpecificRecordBase> producer;

    @Override
    public void handle(UserActionProto userActionProto) {
        UserActionAvro userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(mapTimestampToInstant(userActionProto))
                .setActionType(getActionType(userActionProto.getActionType()))
                .build();

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(topic, null,
                userActionAvro.getTimestamp().toEpochMilli(), userActionAvro.getEventId(),
                userActionAvro);
        producer.send(record);
    }

    private Instant mapTimestampToInstant(UserActionProto userActionProto) {
        return Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos());
    }

    private ActionTypeAvro getActionType(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> null;
        };
    }
}
