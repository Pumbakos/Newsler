package pl.newsler.components.emaillabs;

import pl.newsler.commons.models.NLEmailStatus;

public record GetMailStatus(NLEmailStatus status, boolean error, String errorMessage) {
    public static GetMailStatus of(NLUserMail mail) {
        if (mail.getStatus().equals(NLEmailStatus.ERROR)) {
            return new GetMailStatus(mail.getStatus(), true, mail.getErrorMessage().getValue());
        }

        return new GetMailStatus(mail.getStatus(), false, "");
    }
}
