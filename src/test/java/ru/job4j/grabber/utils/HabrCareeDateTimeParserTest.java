package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

public class HabrCareeDateTimeParserTest {

    @Test
    public void whenParseTextToDate() {
        String dateText = "2023-01-16T17:01:18+03:00";
        LocalDateTime time = new HabrCareeDateTimeParser().parse(dateText);
        assertThat(time).isInstanceOf(LocalDateTime.class);
        assertThat(time).isEqualTo(LocalDateTime.parse(dateText, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}