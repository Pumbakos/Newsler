package pl.newsler.components.signup;

import org.jetbrains.annotations.NotNull;
import pl.newsler.commons.model.NLId;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class StubConfirmationTokenRepository extends InMemoryJpaRepository<NLConfirmationToken, NLId> implements IConfirmationTokenRepository {
    public StubConfirmationTokenRepository() {
        super(NLConfirmationToken::getId);
    }

    @Override
    public <S extends NLConfirmationToken> @NotNull S save(@NotNull S entity) {
        return super.save(entity);
    }

    @Override
    public @NotNull List<NLConfirmationToken> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<NLConfirmationToken> findByToken(final NLToken token) {
        return super.database.values().stream().filter(c -> c.getToken().equals(token)).findFirst();
    }

    @Override
    public void updateConfirmationDate(final NLToken token, final LocalDateTime confirmationDate) {
        super.database.values().stream().filter(c -> c.getToken().equals(token)).findFirst().ifPresent(c -> c.setConfirmationDate(confirmationDate));
    }

    @Override
    public boolean updateTokenExpired(final NLUuid userId) {
        final AtomicBoolean updated = new AtomicBoolean(false);
        super.database.values().stream().filter(c -> c.getUserId().equals(userId)).findFirst().ifPresent(c -> {
            c.setExpirationDate(LocalDateTime.now());
            updated.set(true);
        });

        return updated.get();
    }
}
