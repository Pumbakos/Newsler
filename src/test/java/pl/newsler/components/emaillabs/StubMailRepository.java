package pl.newsler.components.emaillabs;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.models.NLId;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.List;

public class StubMailRepository extends InMemoryJpaRepository<NLUserMail, NLId> implements IMailRepository {
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
    public List<NLUserMail> findAllByUserId(@NotNull NLId userId) {
        return super.database.values().stream().filter(m -> m.getUserId().equals(userId)).toList();
    }
}
