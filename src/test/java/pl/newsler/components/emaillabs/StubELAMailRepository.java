package pl.newsler.components.emaillabs;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.List;

public class StubELAMailRepository extends InMemoryJpaRepository<ELAUserMail, NLUuid> implements IELAMailRepository {
    public StubELAMailRepository() {
        super(ELAUserMail::getUuid);
    }

    @Override
    public <S extends ELAUserMail> @NotNull S save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public @NotNull List<ELAUserMail> findAll() {
        return super.findAll();
    }

    @Override
    public List<ELAUserMail> findAllByUserId(@NotNull NLUuid userId) {
        return super.database.values().stream().filter(m -> m.getUserId().getValue().equals(userId.getValue())).toList();
    }
}
