package pl.newsler.components.user.event;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import pl.ks.hex.common.event.DomainIncomingEvent;
import pl.ks.hex.common.event.DomainIncomingEventSerializer;
import pl.newsler.commons.event.incoming.DomainIncomingEvent;
import pl.newsler.commons.event.incoming.DomainIncomingEventSerializer;
import pl.newsler.commons.models.Email;
import pl.newsler.components.user.NLIUserRepository;
import pl.newsler.components.user.NLId;
import pl.newsler.components.user.NLUser;

@Slf4j
@RequiredArgsConstructor
class EmployeeEventSourceRepository implements NLIUserRepository {
    private final EntityManager entityManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final NLUserEventStreamRepositoryJpa userEventStreamRepositoryJpa;
    private final DomainIncomingEventSerializer domainIncomingEventSerializer;

    @Override
    public NLUser getById(NLId id) {
        NLUserEventStream eventStream = userEventStreamRepositoryJpa.getById(id.getValue());
        return lockAndMap(eventStream);
    }

    @Override
    public List<NLUser> findAll() {
        return userEventStreamRepositoryJpa.findAll().stream()
                .map(this::lockAndMap)
                .toList();
    }

    private NLUser lockAndMap(NLUserEventStream employeeEventStream) {
        entityManager.lock(employeeEventStream, LockModeType.OPTIMISTIC);

        return NLUser.recreate(
                employeeEventStream.getEvents().stream()
                        .map(NLUserEvent::getContent)
                        .map(domainIncomingEventSerializer::deserialize)
                        .sorted(Comparator.comparingInt(DomainIncomingEvent::getSequenceNumber))
                        .collect(Collectors.toList()),
                employeeEventStream.getVersion()
        );
    }

    @Override
    public void save(NLUser user) {
        NLUserEventStream stream = userEventStreamRepositoryJpa
                .findById(user.getId().getValue())
                .orElseGet(() -> {
                    NLUserEventStream employeeEventStream = new NLUserEventStream();
                    employeeEventStream.setEvents(new HashSet<>());
                    employeeEventStream.setUserId(user.getId().getValue());
                    return employeeEventStream;
                });

        if (!Objects.equals(stream.getVersion(), user.getVersion())) {
            throw new OptimisticLockException();
        }

        stream.getEvents().addAll(
                user.getAndClearPendingIncomingEvents().stream()
                        .map(domainIncomingEventSerializer::serialize)
                        .map(bytes -> {
                            NLUserEvent employeeEvent = new NLUserEvent();
                            employeeEvent.setContent(bytes);
                            return employeeEvent;
                        })
                        .toList()
        );


        user.getAndClearPendingOutgoingEvents().forEach(event -> {
            try {
                log.info("Publishing: {}", event);
                applicationEventPublisher.publishEvent(event);
            } catch (RuntimeException e) {
                log.warn("Ignoring exception", e);
            }
        });

        userEventStreamRepositoryJpa.save(stream);
    }

    @Override
    public Optional<NLUser> findByEmail(Email email) {
        return Optional.empty();
    }

    @Override
    public void enableUser(Email email) {

    }
}
