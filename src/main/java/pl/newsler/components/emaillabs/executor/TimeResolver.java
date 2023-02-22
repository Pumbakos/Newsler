package pl.newsler.components.emaillabs.executor;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeResolver {
    /**
     * This is pretty simple solution that not covers date switch or leap year, but is sufficient for now.
     * @return time rounded to the next 'fifth' minute (meaning: if invocation is at 6:31 it will be rounded to 6:35)
     */
    static ZonedDateTime getStartTime() {
        final ZonedDateTime now = ZonedDateTime.now(); // declaration is split to simplify testing
        final String dateTime = now.toString();
        final String[] splitDateTime = dateTime.split("T");
        final String date = splitDateTime[0];
        final String[] time = splitDateTime[1].split(":");
        final int hour = Integer.parseInt(time[0]);
        final int minutes = Integer.parseInt(time[1].split("\\+")[0]);
        final String secondsAndMillis = splitDateTime[1].split("\\+")[1];
        final String roundedTime = date + "T" + getClosestRoundedHour(hour, minutes) + "+" + secondsAndMillis;
        return ZonedDateTime.parse(roundedTime);
    }

    private static CharSequence getClosestRoundedHour(int hour, int minutes) {
        String min;
        String h;

        h = getClosestHour(hour, false);
        if (minutes >= 55) {
            ++hour;
            h = getClosestHour(hour, hour == 24);
        }

        min = getClosestMinute(minutes);

        return h + ":" + min;
    }

    @NotNull
    private static String getClosestHour(final int hour, final boolean shouldZeroHour) {
        String h;
        if (shouldZeroHour) {
            h = "00";
        } else if (hour < 10) {
            h = "0" + hour;
        } else {
            h = String.valueOf(hour);
        }
        return h;
    }

    private static String getClosestMinute(final int minutes) {
        switch (minutes) {
            case 0, 1, 2, 3, 4 -> {
                return "05";
            }
            case 5, 6, 7, 8, 9 -> {
                return "10";
            }
            case 10, 11, 12, 13, 14 -> {
                return "15";
            }
            case 15, 16, 17, 18, 19 -> {
                return "20";
            }
            case 20, 21, 22, 23, 24 -> {
                return "25";
            }
            case 25, 26, 27, 28, 29 -> {
                return "30";
            }
            case 30, 31, 32, 33, 34 -> {
                return "35";
            }
            case 35, 36, 37, 38, 39 -> {
                return "40";
            }
            case 40, 41, 42, 43, 44 -> {
                return "45";
            }
            case 45, 46, 47, 48, 49 -> {
                return "50";
            }
            case 50, 51, 52, 53, 54 -> {
                return "55";
            }
            default -> {
                return "00";
            }
        }
    }
}
