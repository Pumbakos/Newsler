package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.emaillabs.dto.MailSendRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserDataNotFineException;

import java.util.Optional;

@RequiredArgsConstructor
class MailService {
    private final ELATaskExecutor executor;
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;

    void queue(MailSendRequest request) {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.userMail()));
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        executor.queue(optionalUser.get().map().getId(), MailDetails.of(request));
    }

    GetMailStatus getMailStatus(NLId mailId) {
        Optional<NLUserMail> optionalUserMail = mailRepository.findById(mailId);
        NLUserMail nlUserMail = optionalUserMail.orElseThrow(() -> {
            throw new UserDataNotFineException();
        });

        return GetMailStatus.of(nlUserMail);
    }
}
