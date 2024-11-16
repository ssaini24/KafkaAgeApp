package org.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kafka.models.SourceMessage;
import org.kafka.producer.KafkaProducer;
import org.kafka.utility.HelperFunctions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaConsumerTest {

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private HelperFunctions helperFunctions;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsumeMessage_Success() throws Exception {
        String inputMessage = "{\"payload\":{\"dateOfBirth\":\"2000-01-01\"}}";
        SourceMessage sourceMessage = new SourceMessage();
        SourceMessage.Payload payload = new SourceMessage.Payload();
        payload.setDateOfBirth("2000-01-01");
        sourceMessage.setPayload(payload);

        when(objectMapper.readValue(inputMessage, SourceMessage.class)).thenReturn(sourceMessage);
        when(helperFunctions.calculateAge("2000-01-01")).thenReturn(24);

        kafkaConsumer.consumeMessage(inputMessage);

        verify(kafkaProducer).sendToTopic(any(), any(), eq(24));
    }

    @Test
    void testConsumeMessage_EmptyInput() throws Exception {
        kafkaConsumer.consumeMessage("");
        verify(kafkaProducer, never()).sendToTopic(any(), any(), anyInt());
    }

    @Test
    void testConsumeMessage_InvalidJson() throws Exception {
        String invalidInput = "invalid json";
        when(objectMapper.readValue(invalidInput, SourceMessage.class)).thenThrow(JsonProcessingException.class);
        kafkaConsumer.consumeMessage(invalidInput);
        verify(kafkaProducer, never()).sendToTopic(any(), any(), anyInt());
    }

    @Test
    void testConsumeMessage_NullPayload() throws Exception {
        String inputMessage = "{\"payload\":null}";
        when(objectMapper.readValue(inputMessage, SourceMessage.class)).thenReturn(new SourceMessage());
        kafkaConsumer.consumeMessage(inputMessage);
        verify(kafkaProducer, never()).sendToTopic(any(), any(), anyInt());
    }
}