package ru.job4j.grabber.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HabrCareerDateTimeParserTest {

    @Test
    void whenParseOffsetDateTimeThenReturnLocalDateTime() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "2025-12-02T15:01:51+03:00";

        LocalDateTime result = parser.parse(input);

        assertThat(result).isEqualTo(LocalDateTime.of(
                2025, 12, 2,
                15, 1, 51));
    }

    @Test
    void whenParseAnotherOffsetThenStillReturnLocalPartFromString() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "2025-12-02T15:01:51+00:00";

        LocalDateTime result = parser.parse(input);

        assertThat(result).isEqualTo(LocalDateTime.of(
                2025, 12, 2,
                15, 1, 51));
    }

    @Test
    void whenParseZuluOffsetThenReturnLocalPart() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "2025-12-02T15:01:51Z";

        LocalDateTime result = parser.parse(input);

        assertThat(result).isEqualTo(LocalDateTime.of(
                2025, 12, 2,
                15, 1, 51));
    }

    @Test
    void whenParseWithMillisThenKeepNanos() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "2025-12-02T15:01:51.123+03:00";

        LocalDateTime result = parser.parse(input);

        assertThat(result).isEqualTo(LocalDateTime.of(
                2025, 12, 2,
                15, 1, 51, 123_000_000));
    }

    @Test
    void whenNoOffsetThenThrowDateTimeParseException() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "2025-12-02T15:01:51";

        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void whenGarbageThenThrowDateTimeParseException() {
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        String input = "not-a-date";

        assertThatThrownBy(() -> parser.parse(input))
                .isInstanceOf(DateTimeParseException.class);
    }
}
