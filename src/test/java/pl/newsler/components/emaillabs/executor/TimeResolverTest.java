package pl.newsler.components.emaillabs.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;

@SuppressWarnings("java:S5976") // not applicable
class TimeResolverTest {
    @Test
    void shouldGetClosestStartTimeAndRoundMinutes() {
        final Instant parse = Instant.parse("2012-09-26T06:43:00.000000000Z");
        final String stringifyInstant = "2012-09-26T06:45:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesAndHour() {
        final Instant parse = Instant.parse("2012-09-26T06:57:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:00:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesAndHourWhenMidnight() {
        final Instant parse = Instant.parse("2012-09-26T23:57:00.000000000Z");
        final String stringifyInstant = "2012-09-26T00:00:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo05() {
        final Instant parse = Instant.parse("2012-09-26T07:00:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:05:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo10() {
        final Instant parse = Instant.parse("2012-09-26T07:08:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:10:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo15() {
        final Instant parse = Instant.parse("2012-09-26T07:13:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:15:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo20() {
        final Instant parse = Instant.parse("2012-09-26T07:18:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:20:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo25() {
        final Instant parse = Instant.parse("2012-09-26T07:23:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:25:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo30() {
        final Instant parse = Instant.parse("2012-09-26T07:28:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:30:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo35() {
        final Instant parse = Instant.parse("2012-09-26T07:33:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:35:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo40() {
        final Instant parse = Instant.parse("2012-09-26T07:38:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:40:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo45() {
        final Instant parse = Instant.parse("2012-09-26T07:43:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:45:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo50() {
        final Instant parse = Instant.parse("2012-09-26T07:48:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:50:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo55() {
        final Instant parse = Instant.parse("2012-09-26T07:53:00.000000000Z");
        final String stringifyInstant = "2012-09-26T07:55:00.000000000Z";
        final Instant rounded = Instant.parse(stringifyInstant);

        try (MockedStatic<Instant> utilities = Mockito.mockStatic(Instant.class)) {
            utilities.when(Instant::now).thenReturn(parse);
            utilities.when(() -> Instant.parse(stringifyInstant)).thenReturn(rounded);
            Assertions.assertEquals(Instant.now(), parse);

            Instant startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(startTime, rounded);
        }
    }
}