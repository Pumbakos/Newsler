package pl.newsler.components.emaillabs.dto;

import org.springframework.lang.Nullable;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record MailSendRequest(@NotNull String userMail, @NotNull List<String> toAddresses, @Nullable List<String> cc,
                              @Nullable List<String> bcc, @NotNull String subject, @NotNull String message) {
}
