package pl.newsler.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.newsler.commons.models.NLId;

interface UserRepositoryJpa extends JpaRepository<NLUser, NLId>, UserRepository {
}
