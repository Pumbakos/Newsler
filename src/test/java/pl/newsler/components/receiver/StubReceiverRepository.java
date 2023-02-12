package pl.newsler.components.receiver;

import pl.newsler.commons.models.NLUuid;
import pl.newsler.testcommons.InMemoryJpaRepository;

import java.util.List;

public class StubReceiverRepository extends InMemoryJpaRepository<Receiver, NLUuid> implements IReceiverRepository {
    public StubReceiverRepository() {
        super(Receiver::getId);
    }

    @Override
    public List<Receiver> findAllByUserUuid(final NLUuid uuid) {
        return super.database.values().stream().filter(receiver -> receiver.getUserUuid().equals(uuid)).toList();
    }
}
