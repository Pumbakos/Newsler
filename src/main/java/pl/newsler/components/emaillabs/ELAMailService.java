package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.ELAGetMailResponse;
import pl.newsler.components.emaillabs.dto.ELAMailSendRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.commons.exception.InvalidUserDataException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
class ELAMailService implements IELAMailService {
    private final IELATaskExecutor executor;
    private final IUserRepository userRepository;
    private final IELAMailRepository mailRepository;

    @Override
    public void queue(ELAMailSendRequest request) throws InvalidUserDataException {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.from()));
        if (optionalUser.isEmpty()) {
            throw new InvalidUserDataException();
        }

        executor.queue(optionalUser.get().map().getId(), ELAMailDetails.of(request));
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
