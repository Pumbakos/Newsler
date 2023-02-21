package pl.newsler.components.emaillabs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.RestTemplate;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.executor.ELAConcurrentTaskExecutor;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.IELATaskInstantExecutor;
import pl.newsler.components.receiver.IReceiverService;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;
import java.util.Queue;

@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class ELATaskInstantExecutor extends ELAConcurrentTaskExecutor<ELAInstantMailDetails> implements IELATaskInstantExecutor {
    ELATaskInstantExecutor(final Queue<Pair<NLUuid, ELAInstantMailDetails>> queue, final ConcurrentTaskExecutor taskExecutor,
                           final NLIPasswordEncoder passwordEncoder, final IELAMailRepository mailRepository,
                           final IReceiverService receiverService, final IUserRepository userRepository,
                           final RestTemplate restTemplate, final ObjectMapper objectMapper) {
        super(
                queue,
                taskExecutor,
                passwordEncoder,
                mailRepository,
                receiverService,
                userRepository,
                restTemplate,
                objectMapper
        );
    }

    private boolean queueExecution = false;

    @Override
    public void queue(NLUuid userId, ELAInstantMailDetails details) {
        mailRepository.save(ELAUserMail.of(userId, details));
        queue.add(Pair.of(userId, details));
        if (!queueExecution) {
            queueExecution = true;
            executeQueue();
        }
    }

    private void executeQueue() {
        try {
            super.execute(this::execute);
            log.info("Scheduled another task.");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Async
    void execute() {
        Pair<NLUuid, ELAInstantMailDetails> pair = queue.poll();
        if (pair == null) {
            queueExecution = false;
            return;
        }

        getUserAndExecute(pair);
    }
}
