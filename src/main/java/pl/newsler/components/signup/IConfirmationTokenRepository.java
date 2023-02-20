package pl.newsler.components.signup;

import pl.newsler.commons.model.NLVersion;

public interface IConfirmationTokenRepository extends ConfirmationTokenRepositoryJpa {
    NLVersion version = NLVersion.of("0.0.1");
}
