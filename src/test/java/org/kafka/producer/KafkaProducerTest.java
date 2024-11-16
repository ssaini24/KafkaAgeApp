package org.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kafka.utility.Constants;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaProducerTest {

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendToTopic_EvenAge() throws Exception {
        String message = "test message";
        String messageId = "12345";

        when(objectMapper.writeValueAsString(message)).thenReturn(message);

        kafkaProducer.sendToTopic(message, messageId, 24);

        verify(kafkaTemplate).send(Constants.KAFKA_TOPICS.EVEN_TOPIC, message);
    }

    @Test
    void testSendToTopic_OddAge() throws Exception {
        String message = "test message";
        String messageId = "12345";

        when(objectMapper.writeValueAsString(message)).thenReturn(message);

        kafkaProducer.sendToTopic(message, messageId, 25);

        verify(kafkaTemplate).send(Constants.KAFKA_TOPICS.ODD_TOPIC, message);
    }

    @Test
    void testSendToTopic_SerializationError() throws Exception {
        String message = "test message";
        String messageId = "12345";

        when(objectMapper.writeValueAsString(message)).thenThrow(JsonProcessingException.class);

        try {
            kafkaProducer.sendToTopic(message, messageId, 24);
            fail("Expected RuntimeException due to serialization error");
        } catch (RuntimeException e) {
            assertEquals("Error serializing message", e.getMessage());
            verify(kafkaTemplate, never()).send(any(), any());
        }
    }

    @Test
    void testSendToTopic_KafkaSendError() throws Exception {
        String message = "test message";
        String messageId = "12345";

        when(objectMapper.writeValueAsString(message)).thenReturn(message);
        doThrow(new RuntimeException("Kafka send error")).when(kafkaTemplate).send(any(), any());

        try {
            kafkaProducer.sendToTopic(message, messageId, 24);
            fail("Expected RuntimeException due to Kafka send error");
        } catch (RuntimeException e) {
            assertEquals("Error sending message to Kafka", e.getMessage());
            verify(kafkaTemplate).send(Constants.KAFKA_TOPICS.EVEN_TOPIC, message);
        }
    }
}