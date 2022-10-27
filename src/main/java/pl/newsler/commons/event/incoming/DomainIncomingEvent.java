package pl.newsler.commons.event.incoming;

import java.io.Serializable;
import java.time.Instant;

public interface DomainIncomingEvent extends Serializable {
    Instant getWhen();
    Integer getSequenceNumber();
}
