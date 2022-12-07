package pl.newsler.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLId;

import java.util.Optional;

interface UserRepositoryJpa extends JpaRepository<NLUser, NLId>, UserRepository {
    @Query(value = "SELECT u FROM NLUser u where u.email=:email")
    Optional<NLUser> findByEmail(@Param("email") NLEmail email);
}
