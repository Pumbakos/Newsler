package pl.newsler.components.mail.dto;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public record MailSendRequest(@NotNull String userMail, @NotNull Map<String, String> toAddresses, @Nullable List<String> cc,
                              @Nullable List<String> bcc, @NotNull String subject, @NotNull String message) {
}
