package pl.newsler.components.user;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StubUserRepository extends InMemoryJpaRepository<NLUser, NLUuid> implements IUserRepository {
    public StubUserRepository() {
        super(NLUser::getId);
    }

    @Override
    public <S extends NLUser> @NotNull S save(S entity) {
        return super.save(entity);
    }

    @Override
    public @NotNull List<NLUser> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<NLUser> findByEmail(NLEmail email) {
        Optional<Map.Entry<NLUuid, NLUser>> entry = super.database.entrySet().stream()
                .filter(user -> user.getValue()
                        .getEmail()
                        .equals(email))
                .findFirst();
        return entry.map(Map.Entry::getValue);
    }

    @Override
    public void enableUser(final NLUuid uuid) {
        super.database.values().stream().filter(user -> user.getId().equals(uuid)).findFirst().ifPresent(user -> user.setEnabled(true));
    }
}
