package pl.newsler.components.emaillabs;

import lombok.RequiredArgsConstructor;
import pl.newsler.components.user.NLUser;

@RequiredArgsConstructor(staticName = "of")
class ExecutableMailCommand {
    private final MailDetails details;
    private final NLUser user;
}
