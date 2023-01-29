package pl.newsler.components.emaillabs;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.List;

public class StubMailRepository extends InMemoryJpaRepository<NLUserMail, NLUuid> implements IMailRepository {
    public StubMailRepository() {
        super(NLUserMail::getId);
    }

    @Override
    public <S extends NLUserMail> @NotNull S save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public @NotNull List<NLUserMail> findAll() {
        return super.findAll();
    }

    @Override
    public List<NLUserMail> findAllByUserId(@NotNull NLUuid userId) {
        return super.database.values().stream().filter(m -> m.getUserId().equals(userId)).toList();
    }
}
