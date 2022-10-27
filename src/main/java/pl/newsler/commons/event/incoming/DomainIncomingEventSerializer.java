package pl.newsler.commons.event.incoming;

public interface DomainIncomingEventSerializer {
    byte[] serialize(DomainIncomingEvent domainIncomingEvent);

    DomainIncomingEvent deserialize(byte[] bytes);
}
