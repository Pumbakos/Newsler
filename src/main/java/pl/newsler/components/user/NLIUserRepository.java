package pl.newsler.components.user;

import pl.newsler.commons.models.Email;

import java.util.Optional;

public interface NLIUserRepository {
    Optional<NLUser> findByEmail(Email email);

    void enableUser(Email email);
}
