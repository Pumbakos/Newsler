package pl.newsler.components.emaillabs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.newsler.commons.models.NLUuid;

import java.util.List;

public interface ELAMailRepositoryJpa extends JpaRepository<ELAUserMail, NLUuid>, ELAMailRepository {
    @Query(value = "SELECT m FROM ELAUserMail m WHERE m.userId = :userId")
    List<ELAUserMail> findAllByUserId(@Param("userId") NLUuid userId);
}
