package org.kafka.producer;

import org.kafka.models.SourceMessage;

public interface MessageProducer {

    void sendToTopic(String message, String messageId, int age) throws Exception;
}
