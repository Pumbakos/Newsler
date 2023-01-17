package pl.newsler.components.emaillabs.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLUuid;

import java.time.LocalDateTime;

/**
 * ELA - EmailLabs API
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ELASentMailResults {
    NLUuid id;
    NLUuid userId;
    NLEmailStatus status;
    String message;
    LocalDateTime date;
}
