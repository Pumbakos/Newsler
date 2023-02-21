package pl.newsler.components.emaillabs.usecase;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;

public record ELAMailScheduleRequest(@NotNull String from, @NotNull List<String> to, @Nullable List<String> cc,
                                     @Nullable List<String> bcc, @NotNull String subject, @NotNull String message,
                                     @NotNull String dateTime, @NotNull String zone) {
}
