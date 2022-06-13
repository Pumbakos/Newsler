package pl.palubiak.dawid.newsler.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.registration.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    @Query(value = "SELECT ct FROM ConfirmationToken ct WHERE ct.token = :token")
    Optional<ConfirmationToken> findByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE ConfirmationToken c SET c.confirmationDate = :confirmationDate WHERE c.token = :token")
    void updateConfirmationDate(@Param("token") String token, @Param("confirmationDate") LocalDateTime confirmationDate);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c SET c.expirationDate = current_timestamp WHERE c.user = :user AND c.confirmationDate IS NULL")
    boolean updateTokenExpired(@Param("user") User user);
}
