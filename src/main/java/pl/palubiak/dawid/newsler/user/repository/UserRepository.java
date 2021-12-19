package pl.palubiak.dawid.newsler.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.palubiak.dawid.newsler.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
