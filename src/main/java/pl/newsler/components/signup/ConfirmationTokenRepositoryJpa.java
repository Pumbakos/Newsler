package pl.newsler.components.signup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pl.newsler.commons.model.NLId;
import pl.newsler.commons.model.NLToken;
import pl.newsler.commons.model.NLUuid;

import java.time.LocalDateTime;
import java.util.Optional;

interface ConfirmationTokenRepositoryJpa extends JpaRepository<NLConfirmationToken, NLId> {
    @Query(value = "SELECT ct FROM NLConfirmationToken ct WHERE ct.token = :token")
    Optional<NLConfirmationToken> findByToken(@Param("token") NLToken token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE NLConfirmationToken c SET c.confirmationDate = :confirmationDate WHERE c.token = :token")
    void updateConfirmationDate(@Param("token") NLToken token, @Param("confirmationDate") LocalDateTime confirmationDate);

    @Transactional
    @Modifying
    @Query("UPDATE NLConfirmationToken c SET c.expirationDate = current_timestamp WHERE c.userUuid = :userId AND c.confirmationDate IS NULL")
    int updateTokenExpired(@Param("userId") NLUuid userId);
}
