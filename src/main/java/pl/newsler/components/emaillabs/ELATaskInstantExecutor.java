package pl.newsler.components.emaillabs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.model.NLEmailStatus;
import pl.newsler.commons.model.NLStringValue;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAConcurrentTaskExecutor;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.ELARequestBuilder;
import pl.newsler.components.emaillabs.executor.IELATaskInstantExecutor;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class ELATaskInstantExecutor extends ELAConcurrentTaskExecutor<ELAInstantMailDetails> implements IELATaskInstantExecutor {
    @SuppressWarnings("java:S107")
    ELATaskInstantExecutor(final Queue<Pair<NLUuid, ELAInstantMailDetails>> queue, final ConcurrentTaskExecutor taskExecutor,
                           final NLIPasswordEncoder passwordEncoder, final IELAMailRepository mailRepository,
                           final IReceiverService receiverService, final IUserRepository userRepository,
                           final RestTemplate restTemplate, final ELARequestBuilder paramBuilder) {
        super(
                queue,
                taskExecutor,
                passwordEncoder,
                mailRepository,
                receiverService,
                userRepository,
                restTemplate,
                paramBuilder
        );
    }

    private final AtomicBoolean queueExecution = new AtomicBoolean(false);

    @Override
    public void queue(NLUuid userId, ELAInstantMailDetails details) {
        mailRepository.save(ELAUserMail.of(userId, details));
        queue.add(Pair.of(userId, details));
        if (!queueExecution.get()) {
            queueExecution.set(true);
            executeQueue();
        }
    }

    void executeQueue() {
        try {
            super.execute(this::execute);
            log.info("Scheduled another task.");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void execute() {
        Pair<NLUuid, ELAInstantMailDetails> pair = queue.poll();
        if (pair == null) {
            queueExecution.set(false);
            return;
        }

        try {
            getUserAndExecute(pair);
        } catch (Exception e) {
            final Optional<ELAUserMail> optionalELAUserMail = mailRepository
                    .findAllByUserId(pair.getKey())
                    .stream().filter(mail -> mail.getUuid().equals(pair.getValue().id()))
                    .findFirst();

            final ELAUserMail userMail;
            userMail = optionalELAUserMail.orElseGet(() -> ELAUserMail.of(pair.getKey(), pair.getValue()));
            userMail.setErrorMessage(NLStringValue.of("Something went wrong during preparing you mail :("));
            userMail.setStatus(NLEmailStatus.ERROR);
            mailRepository.save(userMail);
        }
        execute();
    }
}
