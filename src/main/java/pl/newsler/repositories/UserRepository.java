package pl.newsler.repositories;

import pl.newsler.models.user.NLUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<NLUser, Long> {
    @Query(value = "SELECT u FROM NLUser u WHERE u.email = :email")
    Optional<NLUser> findByEmail(@Param("email") String email);

    @Query(value = "SELECT u FROM NLUser u WHERE u.email = :email AND u.password = :password")
    Optional<NLUser> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Transactional
    @Modifying
    @Query("UPDATE NLUser u SET u.enabled = TRUE WHERE u.email = :email")
    void enableUser(@Param("email") String email);
}
