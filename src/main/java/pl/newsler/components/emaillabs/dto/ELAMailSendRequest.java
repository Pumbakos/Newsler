package pl.newsler.components.emaillabs.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.util.List;

public record ELAMailSendRequest(@NotNull String from, @NotNull List<String> to, @Nullable List<String> cc,
                                 @Nullable List<String> bcc, @NotNull String subject, @NotNull String message) {
}
