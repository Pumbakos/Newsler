package pl.newsler.components.emaillabs.usecase;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ELAMailSendRequest(@NotNull String from, @NotNull List<String> to,
                                 @NotNull String subject, @NotNull String message) {
}
