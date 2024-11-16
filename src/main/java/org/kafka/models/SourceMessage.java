package org.kafka.models;

import lombok.Data;

@Data
public class SourceMessage {

    Payload payload;

    @Data
    public static class Payload{
        String messageId;
        String name;
        String address;
        String dateOfBirth;
    }
}
