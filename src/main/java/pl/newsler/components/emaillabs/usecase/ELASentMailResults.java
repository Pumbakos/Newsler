package pl.newsler.components.emaillabs.usecase;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.newsler.commons.model.NLEmailStatus;
import pl.newsler.commons.model.NLUuid;

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
