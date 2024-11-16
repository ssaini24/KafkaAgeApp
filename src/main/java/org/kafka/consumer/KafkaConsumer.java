package org.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kafka.models.SourceMessage;
import org.kafka.producer.KafkaProducer;
import org.kafka.utility.HelperFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Objects;

@Service
public class KafkaConsumer implements MessageConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaProducer kafkaProducer;

    @Autowired
    HelperFunctions helperFunctions;

    @Override
    @KafkaListener(topics = "${source.topic}", groupId = "${spring.kafka.source.consumer.group-id}")
    public void consumeMessage(String inputMessage) {
        if (Objects.isNull(inputMessage) || inputMessage.isEmpty()){
            LOGGER.info("found empty message: returning");
            return;
        }

        LOGGER.info("Processing received message: {} ", inputMessage);
        SourceMessage sourceMessage = null;
        try {
            sourceMessage = deSerializeMessage(inputMessage);
        } catch (Exception e) {
            LOGGER.error("error while parsing input message {}", inputMessage, e);
            return;
        }

        if (Objects.isNull(sourceMessage) || Objects.isNull(sourceMessage.getPayload())) {
            LOGGER.info("found null upon parsing message {}: returning", inputMessage);
            return;
        }

        processSourceMessage(sourceMessage);
    }

    private void processSourceMessage(SourceMessage sourceMessage){
        String messageId = helperFunctions.generateMessageId();
        SourceMessage.Payload message = sourceMessage.getPayload();
        message.setMessageId(messageId);
        LOGGER.info("Mapping received message: {} to messageId {}", sourceMessage.toString(), messageId);

        int age = helperFunctions.calculateAge(sourceMessage.getPayload().getDateOfBirth());
        try {
            kafkaProducer.sendToTopic(sourceMessage.toString(), messageId, age);
        } catch (Exception e) {
            LOGGER.error("error while sending message to downstream kafka topics messageId: {}", messageId, e);
            throw new RuntimeException(e);
        }

        LOGGER.info("successfully processed messageId {}", messageId);
    }

    private SourceMessage deSerializeMessage(String message) throws Exception {
        try {
            return objectMapper.readValue(message, SourceMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}


