package pl.palubiak.dawid.newsler.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.repository.BusinessClientRepository;
import pl.palubiak.dawid.newsler.generators.BusinessClientGenerator;

import java.util.Optional;

@ContextConfiguration(classes = {BusinessClientGenerator.class, PresenceChecker.class, BusinessClientRepository.class, BusinessClient.class})
@SpringBootTest
public class PresenceCheckerTest {
    @MockBean
    private PresenceChecker<BusinessClient> presenceChecker;

    @MockBean
    private BusinessClientRepository businessClientRepository;

    @Test
    @DisplayName("Should return Optional.empty() if client is present")
    public void checkIfClientIsPresent() {
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClient();
        Mockito.when(presenceChecker.checkIfPresent(businessClientRepository, businessClient)).thenReturn(Optional.empty());
        Optional<BusinessClient> result = presenceChecker.checkIfPresent(businessClientRepository, businessClient);
        Mockito.verify(presenceChecker).checkIfPresent(businessClientRepository, businessClient);

        Assertions.assertEquals(Optional.empty(), result);
    }

    @Test
    @DisplayName("Should return client instance if client is not present")
    public void checkIfClientIsPresentAndSaveIt() {
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClientWithNullId();
        Mockito.when(presenceChecker.checkIfPresent(businessClientRepository, businessClient)).thenReturn(Optional.of(businessClient));
        Optional<BusinessClient> result = presenceChecker.checkIfPresent(businessClientRepository, businessClient);
        Mockito.verify(presenceChecker).checkIfPresent(businessClientRepository, businessClient);

        Assertions.assertEquals(Optional.of(businessClient), result);
    }

    @Test
    @DisplayName("Should throw NullPointerException if client is null")
    public void checkIfClientIsPresentAndThrowsNPE() {
        Mockito.when(presenceChecker.checkIfPresent(businessClientRepository, null)).thenThrow(NullPointerException.class);

        Assertions.assertThrows(NullPointerException.class, () -> {presenceChecker.checkIfPresent(businessClientRepository, null);});
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if client's ID is out of range")
    public void checkIfClientIsPresentAndThrowsIAE() {
        BusinessClient businessClient = BusinessClientGenerator.createBusinessClientWithNullId();
        Mockito.when(presenceChecker.checkIfPresent(businessClientRepository, businessClient)).thenThrow(IllegalArgumentException.class);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {presenceChecker.checkIfPresent(businessClientRepository, businessClient);});
    }
}
