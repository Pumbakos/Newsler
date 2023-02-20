package pl.newsler.components.emaillabs.usecase;

import java.util.List;

public record ELAGetMailResponse(
        String uuid,
        String from,
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String subject,
        String message,
        String status
) {
}
