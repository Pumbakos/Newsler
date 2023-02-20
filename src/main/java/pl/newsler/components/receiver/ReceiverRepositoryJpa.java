package pl.newsler.components.receiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLUuid;

import java.util.List;
import java.util.Optional;

interface ReceiverRepositoryJpa extends JpaRepository<Receiver, NLUuid> {
    @Query(value = "SELECT r FROM Receiver r WHERE r.userUuid = :uuid")
    List<Receiver> findAllByUserUuid(@Param("uuid") NLUuid uuid);

    @Query(value = "SELECT r FROM Receiver r WHERE r.userUuid = :userUuid AND r.email = :email")
    Optional<Receiver> findByUserUuidAndEmail(@Param("userUuid") NLUuid userUuid, @Param("email") NLEmail email);
}
