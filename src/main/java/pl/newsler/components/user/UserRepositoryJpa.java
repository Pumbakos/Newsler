package pl.newsler.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.newsler.commons.models.NLEmail;
import pl.newsler.commons.models.NLUuid;

import java.util.Optional;

interface UserRepositoryJpa extends JpaRepository<NLUser, NLUuid>, UserRepository {
    @Query(value = "SELECT u FROM NLUser u where u.email=:email")
    Optional<NLUser> findByEmail(@Param("email") NLEmail email);

    @Transactional
    @Modifying
    @Query("UPDATE NLUser u SET u.enabled = TRUE WHERE u.id = :uuid")
    void enableUser(@Param("uuid") NLUuid uuid);
}
