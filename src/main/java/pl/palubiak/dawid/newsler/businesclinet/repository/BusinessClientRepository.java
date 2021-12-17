package pl.palubiak.dawid.newsler.businesclinet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessClientRepository extends JpaRepository<BusinessClient, Long> {
    @Query(value = "SELECT c FROM BusinessClient c WHERE c.email = :email")
    Optional<BusinessClient> findByEmail(@Param("email") String email);

    @Query(value = "SELECT c FROM BusinessClient c WHERE c.name = :name")
    Optional<BusinessClient> findByName(@Param("name") String name);

    @Query(value = "SELECT c FROM BusinessClient c WHERE c.activeNewsLetters = true")
    List<BusinessClient> findAllByActiveNewsletter();

    @Query(value = "SELECT c FROM BusinessClient c WHERE c.activePartnershipOffers = true")
    List<BusinessClient> findAllByActivePartnershipOffers();

    @Query(value = "SELECT c FROM BusinessClient c WHERE c.activeNewsLetters = true AND c.activePartnershipOffers = true")
    List<BusinessClient> findAllByActiveNewsletterAndActivePartnershipOffers();
}
