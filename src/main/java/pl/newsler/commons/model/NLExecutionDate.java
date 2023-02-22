package pl.newsler.commons.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE, force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class NLExecutionDate implements NLModel {
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final String value;

    public static NLExecutionDate of(LocalDateTime dateTime) {
        return new NLExecutionDate(dateTime.format(DateTimeFormatter.ofPattern(PATTERN)));
    }

    public static NLExecutionDate of(ZonedDateTime dateTime) {
        return new NLExecutionDate(dateTime.format(DateTimeFormatter.ofPattern(PATTERN)));
    }

    @Override
    public boolean validate() {
        if(value == null) {
            return false;
        }
        try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}