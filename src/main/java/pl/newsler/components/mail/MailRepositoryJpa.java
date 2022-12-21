package pl.newsler.components.mail;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.newsler.commons.models.NLId;

public interface MailRepositoryJpa extends JpaRepository<NLUserMail, NLId>, MailRepository {
}
