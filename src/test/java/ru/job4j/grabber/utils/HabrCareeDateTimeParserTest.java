package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class HabrCareeDateTimeParserTest {

    @Test
    public void whenParseTextToDate() {
        String dateText = "2023-01-16T17:01:18+03:00";
        LocalDateTime time = new HabrCareeDateTimeParser().parse(dateText);
        assertThat(time).isEqualTo("2023-01-16T17:01:18");
    }
}