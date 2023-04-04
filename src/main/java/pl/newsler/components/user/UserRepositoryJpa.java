package pl.newsler.components.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.newsler.commons.model.NLEmail;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;

import java.util.Optional;

interface UserRepositoryJpa extends JpaRepository<NLUser, NLUuid>, UserRepository {
    @Query(value = "SELECT u FROM NLUser u where u.email=:email")
    Optional<NLUser> findByEmail(@Param("email") NLEmail email);

    @Query(value = "SELECT u FROM NLUser u where u.subscriptionToken=:token")
    Optional<NLUser> findBySubscriptionToken(@Param("token") NLToken token);

    @Transactional
    @Modifying
    @Query("UPDATE NLUser u SET u.enabled = TRUE WHERE u.uuid = :uuid")
    void enableUser(@Param("uuid") NLUuid uuid);
}
