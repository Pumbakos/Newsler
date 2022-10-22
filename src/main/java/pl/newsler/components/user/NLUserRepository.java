package pl.newsler.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.newsler.commons.models.Email;

import java.util.Optional;

public interface NLUserRepository extends NLIUserRepository, JpaRepository<NLUser, NLId> {
    @Override
    @Query(value = "SELECT u FROM NLUser u WHERE u.email = :email")
    Optional<NLUser> findByEmail(@Param("email") Email email);

    @Override
    @Transactional
    @Modifying
    @Query(value = "UPDATE NLUser u SET u.enabled = true WHERE u.email = :email")
    void enableUser(Email email);
}
