package pl.newsler.components.emaillabs.dto;

import jakarta.validation.constraints.NotNull;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.components.emaillabs.NLUserMail;

public record GetMailStatus(@NotNull NLEmailStatus status, boolean error, @NotNull String errorMessage) {
    public static GetMailStatus of(@NotNull NLUserMail mail) {
        if (mail.getStatus().equals(NLEmailStatus.ERROR)) {
            return new GetMailStatus(mail.getStatus(), true, mail.getErrorMessage().getValue());
        }

        return new GetMailStatus(mail.getStatus(), false, "");
    }
}
