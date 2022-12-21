package pl.newsler.components.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import pl.newsler.commons.models.NLAppKey;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLSecretKey;
import pl.newsler.commons.models.NLSmtpAccount;
import pl.newsler.components.mail.dto.MailSendRequest;
import pl.newsler.components.user.IUserRepository;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.UserDataNotFineException;
import pl.newsler.security.NLIPasswordEncoder;

import java.util.Optional;

@RequiredArgsConstructor
class MailService {
    private final TaskExecutor executor;
    private final IUserRepository userRepository;
    private final IMailRepository mailRepository;
    private final NLIPasswordEncoder passwordEncoder;

    void queue(MailSendRequest request) {
        Optional<NLUser> optionalUser = userRepository.findByEmail(NLEmail.of(request.userMail()));
        if (optionalUser.isEmpty()) {
            throw new UserDataNotFineException();
        }

        NLUser user = optionalUser.get();
        user.setAppKey(NLAppKey.of(passwordEncoder.decrypt(user.getAppKey().getValue())));
        user.setSecretKey(NLSecretKey.of(passwordEncoder.decrypt(user.getSecretKey().getValue())));
        user.setSmtpAccount(NLSmtpAccount.of(passwordEncoder.decrypt(user.getSmtpAccount().getValue())));

        ExecutableMailCommand command = ExecutableMailCommand.of(user, MailDetails.of(request));
        executor.execute(command);
    }
}
