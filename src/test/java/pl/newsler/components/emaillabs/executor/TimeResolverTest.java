package pl.newsler.components.emaillabs.executor;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Random;

class TimeResolverTest {
    private final Random random = new SecureRandom();
    @Test
    void shouldTestTimeResolverForNow() {
        ZonedDateTime now = ZonedDateTime.now();
        for (int i = 0; i < 24; i++) {
            ZonedDateTime time = TimeResolver.getStartTime(now);
            Assertions.assertTrue(String.valueOf(time.getMinute()).endsWith("5") || String.valueOf(time.getMinute()).endsWith("0"));
            Assertions.assertEquals(0, time.getSecond());
            Assertions.assertEquals(0, time.getNano());
            Assertions.assertNotNull(time);
            now = now.plusHours(i);
        }
    }

    @RepeatedTest(5)
    void shouldTestTimeResolverWithRandomMinutesAndSeconds() {
        ZonedDateTime now = ZonedDateTime.now().withMinute(range0to60()).withSecond(range0to60());
        for (int i = 0; i < 24; i++) {
            ZonedDateTime time = TimeResolver.getStartTime(now);
            Assertions.assertTrue(String.valueOf(time.getMinute()).endsWith("5") || String.valueOf(time.getMinute()).endsWith("0"));
            Assertions.assertEquals(0, time.getSecond());
            Assertions.assertEquals(0, time.getNano());
            Assertions.assertNotNull(time);
            now = now.plusHours(i).withMinute(range0to60()).withSecond(range0to60());
        }
    }

    int range0to60() {
        return random.nextInt(0, 60);
    }
}