package pl.newsler.components.user;

import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.Map;
import java.util.Optional;

class MockUserRepository extends InMemoryJpaRepository<NLUser, NLId> implements UserRepository {
    MockUserRepository() {
        super(NLUser::getId);
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
