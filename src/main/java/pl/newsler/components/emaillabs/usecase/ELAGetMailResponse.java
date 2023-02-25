package pl.newsler.components.emaillabs.usecase;

import java.util.List;

public record ELAGetMailResponse(
        String uuid,
        String from,
        List<String> to,
        String subject,
        String message,
        String status
) {
}
