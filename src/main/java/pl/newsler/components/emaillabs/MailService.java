package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.emaillabs.exceptions.ELAMailNotFoundException;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.api.exceptions.UserDataNotFineException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
class MailService implements IMailService {
    private final IELATaskExecutor executor;
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;

    @Override
    public void queue(MailSendRequest request) throws UserDataNotFineException {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.userMail()));
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        executor.queue(optionalUser.get().map().getId(), MailDetails.of(request));
    }

    @Override
    public List<NLUserMail> fetchAllMails(NLUuid userId) {
        userRepository.findById(userId).orElseThrow(UserDataNotFineException::new);

        return mailRepository.findAllByUserId(userId);
    }

    @Override
    public GetMailStatus getMailStatus(NLUuid mailId, NLUuid userId) throws UserDataNotFineException, ELAMailNotFoundException {
        Optional<NLUser> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException("User not found.");
        }

        Optional<NLUserMail> optionalMail = mailRepository.findAllByUserId(userId).stream().filter(m -> m.getId().equals(mailId)).findFirst();
        if (optionalMail.isEmpty()) {
            throw new ELAMailNotFoundException("MAIL ID", "Mail with given ID not found.");
        }

        return GetMailStatus.of(optionalMail.get());
    }
}
