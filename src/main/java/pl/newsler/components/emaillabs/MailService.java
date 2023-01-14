package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.emaillabs.dto.GetMailStatus;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.emaillabs.exceptions.ELAMailNotFoundException;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserDataNotFineException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
class MailService implements IMailService {
    private final ELATaskExecutor executor;
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;

    @Override
    public void queue(MailSendRequest request) {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.userMail()));
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        executor.queue(optionalUser.get().map().getId(), MailDetails.of(request));
    }

    @Override
    public List<NLUserMail> fetchAllMails(NLId userId) {
        return mailRepository.findAll();
    }

    @Override
    public GetMailStatus getMailStatus(NLId mailId, NLId userId) {
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
