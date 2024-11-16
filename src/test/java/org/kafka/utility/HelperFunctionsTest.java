package org.kafka.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelperFunctionsTest {

    private final HelperFunctions helperFunctions = new HelperFunctions();

    @Test
    void testCalculateAge_ValidDate() {
        String dateOfBirth = "2000-01-01";
        int age = helperFunctions.calculateAge(dateOfBirth);
        assertEquals(24, age);
    }

    @Test
    void testCalculateAge_BirthDateToday() {
        String dateOfBirth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int age = helperFunctions.calculateAge(dateOfBirth);
        assertEquals(0, age);
    }

    @Test
    void testCalculateAge_InvalidDateFormat() {
        String invalidDate = "01/01/2000";

        try {
            helperFunctions.calculateAge(invalidDate);
        } catch (Exception e) {
            assertEquals(java.time.format.DateTimeParseException.class, e.getClass());
        }
    }

    @Test
    void testGenerateMessageId() {
        String messageId = helperFunctions.generateMessageId();

        assertNotNull(messageId);
        assertEquals(36, messageId.length());
    }
}