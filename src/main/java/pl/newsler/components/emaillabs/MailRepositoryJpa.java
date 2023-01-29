package pl.newsler.components.emaillabs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.newsler.commons.models.NLUuid;

import java.util.List;

public interface MailRepositoryJpa extends JpaRepository<NLUserMail, NLUuid>, MailRepository {
    @Query(value = "SELECT m FROM NLUserMail m WHERE m.userId = :userId")
    List<NLUserMail> findAllByUserId(@Param("userId") NLUuid userId);
}
