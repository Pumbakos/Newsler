package pl.newsler.components.receiver;

import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;
import pl.newsler.commons.models.NLVersion;

import java.util.List;
import java.util.Optional;

public interface IReceiverRepository extends ReceiverRepositoryJpa {
    NLVersion version = NLVersion.of("0.0.1");
    List<Receiver> findAllByUserUuid(final NLUuid uuid);

    Optional<Receiver> findByUserUuidAndEmail(final NLUuid userUuid, final NLEmail email);
}
