package pl.newsler.components.receiver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.newsler.commons.models.NLUuid;

import java.util.List;

interface ReceiverRepositoryJpa extends JpaRepository<Receiver, NLUuid> {
    @Query(value = "SELECT r FROM Receiver r WHERE r.userUuid = :uuid")
    List<Receiver> findAllByUserUuid(@Param("uuid") NLUuid uuid);
}
