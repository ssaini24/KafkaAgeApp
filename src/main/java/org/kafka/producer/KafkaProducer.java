package org.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kafka.utility.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer implements MessageProducer {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendToTopic(String message, String messageId, int age) throws Exception {
        String targetTopic = determineTargetTopic(age);
        LOGGER.info("sending message to kafka topic {} messageId {}", targetTopic, messageId);

        String jsonMessage = "";
        try {
            jsonMessage = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing message for topic: {}", targetTopic, e);
            throw new RuntimeException("Error serializing message", e);
        }

        try {
            pushToKafka(targetTopic, jsonMessage);
            LOGGER.info("Message sent successfully with messageId: {} to topic: {}", messageId, targetTopic);
        } catch (Exception e) {
            LOGGER.error("Failed to send message with messageId: {} to topic: {}", messageId, targetTopic, e);
            throw new RuntimeException("Error sending message to Kafka", e);
        }
    }

    private String determineTargetTopic(int age) {
        return (age % 2 == 0) ? Constants.KAFKA_TOPICS.EVEN_TOPIC : Constants.KAFKA_TOPICS.ODD_TOPIC;
    }

    private void pushToKafka(String targetTopic, String message) throws Exception {
        kafkaTemplate.send(targetTopic, message);
    }
}
