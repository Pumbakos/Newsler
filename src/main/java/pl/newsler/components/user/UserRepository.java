package pl.newsler.components.user;

import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLVersion;

import java.util.Optional;

interface UserRepository {
    NLVersion version = NLVersion.of("0.0.1");
    Optional<NLUser> findById(NLId id);
    <S extends NLUser> S save(S user);
    Optional<NLUser> findByEmail(NLEmail email);

    void deleteById(NLId id);
}
