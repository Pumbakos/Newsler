package pl.newsler.components.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.commons.models.NLPassword;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.Map;
import java.util.Optional;

class MockUserRepository extends InMemoryJpaRepository<NLUser, NLId> implements UserRepository {
    private final PasswordEncoder passwordEncoder;
    MockUserRepository(PasswordEncoder passwordEncoder) {
        super(NLUser::getId);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public <S extends NLUser> S save(S entity) {
        entity.setPassword(NLPassword.of(passwordEncoder.encode(entity.getPassword())));
        return super.save(entity);
    }

    @Override
    public Optional<NLUser> findByEmail(NLEmail email) {
        Optional<Map.Entry<NLId, NLUser>> entry = super.database.entrySet().stream()
                .filter(user -> user.getValue()
                        .getEmail()
                        .equals(email))
                .findFirst();
        if (entry.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(entry.get().getValue());
    }
}
