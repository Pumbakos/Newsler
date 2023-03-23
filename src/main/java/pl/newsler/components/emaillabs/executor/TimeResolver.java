package pl.newsler.components.emaillabs.executor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeResolver {
    /**
     * @return next timestamp rounded to fifth minute
     */
    static ZonedDateTime getStartTime(ZonedDateTime now) {
        final String dateTime = now.toString();
        final String[] splitDateTime = dateTime.split("T");
        final String[] time = splitDateTime[1].split(":");
        final int currentMinute = Integer.parseInt(time[1].split("\\+")[0]);
        final int minutes = Math.abs(currentMinute - getClosestMinute(currentMinute));

        return now.plusMinutes(minutes).withSecond(0).withNano(0);
    }

    private static int getClosestMinute(final int minutes) {
        switch (minutes) {
            case 0, 1, 2, 3, 4 -> {
                return 5;
            }
            case 5, 6, 7, 8, 9 -> {
                return 10;
            }
            case 10, 11, 12, 13, 14 -> {
                return 15;
            }
            case 15, 16, 17, 18, 19 -> {
                return 20;
            }
            case 20, 21, 22, 23, 24 -> {
                return 25;
            }
            case 25, 26, 27, 28, 29 -> {
                return 30;
            }
            case 30, 31, 32, 33, 34 -> {
                return 35;
            }
            case 35, 36, 37, 38, 39 -> {
                return 40;
            }
            case 40, 41, 42, 43, 44 -> {
                return 45;
            }
            case 45, 46, 47, 48, 49 -> {
                return 50;
            }
            case 50, 51, 52, 53, 54 -> {
                return 55;
            }
            default -> {
                return 60;
            }
        }
    }
}
