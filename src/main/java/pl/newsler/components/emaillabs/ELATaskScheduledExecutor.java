package pl.newsler.components.emaillabs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.model.NLExecutionDate;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAConcurrentTaskExecutor;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.executor.ELAScheduleMailDetails;
import pl.newsler.components.emaillabs.executor.IELATaskScheduledExecutor;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ELATaskScheduledExecutor extends ELAConcurrentTaskExecutor<ELAScheduleMailDetails> implements IELATaskScheduledExecutor {
    @SuppressWarnings("java:S107")
    ELATaskScheduledExecutor(final Queue<Pair<NLUuid, ELAScheduleMailDetails>> queue, final ConcurrentTaskScheduler taskScheduler,
                             final NLIPasswordEncoder passwordEncoder, final IELAMailRepository mailRepository,
                             final IReceiverService receiverService, final IUserRepository userRepository,
                             final RestTemplate restTemplate, final ELARequestBuilder paramBuilder) {
        super(
                queue,
                taskScheduler,
                passwordEncoder,
                mailRepository,
                receiverService,
                userRepository,
                restTemplate,
                paramBuilder
        );

        super.scheduleAtFixedRate(this::scanQueue, Duration.ofMinutes(5L));
    }

    @Override
    public void schedule(final NLUuid userId, final ELAScheduleMailDetails details) {
        mailRepository.save(ELAUserMail.of(userId, details));
        queue.add(Pair.of(userId, details));
    }

    void scanQueue() {
        final ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        log.info("{} | Scanning queue...", ZonedDateTime.of(LocalDateTime.now(), zoneId).format(DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN)));
        final ZonedDateTime now = ZonedDateTime.now(zoneId);
        final LinkedList<Pair<NLUuid, ELAScheduleMailDetails>> toBeExecuted = new LinkedList<>();
        Iterator<Pair<NLUuid, ELAScheduleMailDetails>> iterator = queue.iterator();

        while (iterator.hasNext()) {
            Pair<NLUuid, ELAScheduleMailDetails> next = iterator.next();
            ZonedDateTime scheduleTime = next.getRight().zonedDateTime();
            if (shouldBeSent(now, scheduleTime)) {
                toBeExecuted.push(next);
                iterator.remove();
            }
        }

        executeSchedule(toBeExecuted);
    }

    private static boolean shouldBeSent(final ZonedDateTime now, final ZonedDateTime scheduleTime) {
        return scheduleTime.isBefore(now) || scheduleTime.isEqual(now);
    }

    private void executeSchedule(final LinkedList<Pair<NLUuid, ELAScheduleMailDetails>> toBeExecuted) {
        for (final Pair<NLUuid, ELAScheduleMailDetails> pair : toBeExecuted) {
            getUserAndExecute(pair);
        }
    }
}
