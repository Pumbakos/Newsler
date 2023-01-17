package pl.newsler.components.user;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;
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
}
