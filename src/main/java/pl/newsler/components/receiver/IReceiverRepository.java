package pl.newsler.components.receiver;

import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLVersion;

import java.util.List;

public interface IReceiverRepository extends ReceiverRepositoryJpa {
    NLVersion version = NLVersion.of("0.0.1");
    List<Receiver> findAllByUserUuid(NLUuid uuid);
}
