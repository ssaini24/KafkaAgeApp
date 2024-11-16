package org.kafka.consumer;

public interface MessageConsumer {

    void consumeMessage(String message);
}
