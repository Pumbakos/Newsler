package pl.newsler.components.emaillabs.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.ZonedDateTime;

@SuppressWarnings("java:S5976") // not applicable
class TimeResolverTest {
    @Test
    void shouldGetClosestStartTimeAndRoundMinutes() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T06:43+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T06:45+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);
            Assertions.assertEquals(ZonedDateTime.now(), parse);

            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesAndHour() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T06:57+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:00+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesAndHourWhenMidnight() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T23:57+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-27T00:00+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo05() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:00+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:05+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo10() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:08+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:10+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo15() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:13+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:15+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo20() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:18+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:20+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo25() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:23+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:25+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo30() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:28+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:30+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo35() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:33+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:35+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo40() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:38+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:40+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo45() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:43+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:45+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo50() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:48+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:50+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }

    @Test
    void shouldGetClosestStartTimeAndRoundMinutesTo55() {
        String zoneOffset = ZonedDateTime.now().toString().split("\\+")[1];
        final ZonedDateTime parse = ZonedDateTime.parse(String.format("2012-09-26T07:53+%s", zoneOffset));
        final String stringifyInstant = String.format("2012-09-26T07:55+%s", zoneOffset);
        final ZonedDateTime rounded = ZonedDateTime.parse(stringifyInstant);

        try (MockedStatic<ZonedDateTime> utilities = Mockito.mockStatic(ZonedDateTime.class)) {
            utilities.when(ZonedDateTime::now).thenReturn(parse);
            utilities.when(() -> ZonedDateTime.parse(rounded.toString())).thenReturn(rounded);

            Assertions.assertEquals(ZonedDateTime.now(), parse);
            ZonedDateTime startTime = TimeResolver.getStartTime();
            Assertions.assertEquals(rounded, startTime);
        }
    }
}