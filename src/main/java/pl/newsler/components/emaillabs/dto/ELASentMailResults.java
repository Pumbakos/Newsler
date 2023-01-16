package pl.newsler.components.emaillabs.dto;

import lombok.AllArgsConstructor;
import lombok.Value;
import pl.newsler.commons.models.NLEmailStatus;
import pl.newsler.commons.models.NLId;

import java.time.LocalDateTime;

/**
 * ELA - EmailLabs API
 */
@Value
@AllArgsConstructor(staticName = "of")
public class ELASentMailResults {
    NLId id;
    NLId userId;
    NLEmailStatus status;
    String message;
    LocalDateTime date;
}
