package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import pl.newsler.commons.exception.InvalidUserDataException;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLExecutionDate;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.components.emaillabs.exception.InvalidDateException;
import pl.newsler.components.emaillabs.executor.ELAInstantMailDetails;
import pl.newsler.components.emaillabs.executor.ELAScheduleMailDetails;
import pl.newsler.components.emaillabs.executor.IELATaskInstantExecutor;
import pl.newsler.components.emaillabs.executor.IELATaskScheduledExecutor;
import pl.newsler.components.emaillabs.usecase.ELAGetMailResponse;
import pl.newsler.components.emaillabs.usecase.ELAMailSendRequest;
import pl.newsler.components.emaillabs.usecase.ELAMailScheduleRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
class ELAMailService implements IELAMailService {
    private final IELATaskInstantExecutor instantExecutor;
    private final IELATaskScheduledExecutor scheduledExecutor;
    private final IUserRepository userRepository;
    private final IELAMailRepository mailRepository;

    @Override
    public void queue(ELAMailSendRequest request) throws InvalidUserDataException {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.from()));
        if (optionalUser.isEmpty()) {
            throw new InvalidUserDataException();
        }

        instantExecutor.queue(optionalUser.get().map().getId(), ELAInstantMailDetails.of(request));
    }

    @Override
    public void schedule(final ELAMailScheduleRequest request) throws InvalidUserDataException, InvalidDateException {
        String dateTime = request.dateTime();
        if (StringUtils.isBlank(dateTime)) {
            throw new InvalidDateException();
        }
        try {
            final LocalDateTime time = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(NLExecutionDate.PATTERN));

            Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.from()));
            if (optionalUser.isEmpty()) {
                throw new InvalidUserDataException();
            }

            Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();
            boolean contains = availableZoneIds.contains(request.zone());

            scheduledExecutor.schedule(
                    optionalUser.get().map().getId(),
                    ELAScheduleMailDetails.of(request, time.atZone(ZoneId.of(contains ? request.zone() : "Europe/Warsaw")))
            );
        } catch (DateTimeException e) {
            throw new InvalidDateException();
        }
    }

    @Override
    public List<ELAGetMailResponse> fetchAllMails(NLUuid userId) throws InvalidUserDataException {
        Optional<NLUser> optional = userRepository.findById(userId);
        if (optional.isEmpty()) {
            throw new InvalidUserDataException();
        }

        return mailRepository.findAllByUserId(userId).stream().map(from -> from.toResponse(optional.get().getEmail().getValue())).toList();
    }
}
