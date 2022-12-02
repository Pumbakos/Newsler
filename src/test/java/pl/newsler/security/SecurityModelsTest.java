package pl.newsler.security;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pl.newsler.commons.models.NLId;
import pl.newsler.components.user.NLUser;
import pl.newsler.components.user.TestUserFactory;

import javax.security.auth.Subject;
import java.util.HashSet;
import java.util.UUID;

@SuppressWarnings({"java:S5778"})
class SecurityModelsTest {
    private final TestUserFactory factory = new TestUserFactory();

    @Test
    void shouldCreateAndCompareNLPrincipal() {
        NLUser dotted = factory.dotted();
        dotted.setId(NLId.of(UUID.randomUUID()));
        NLUser dashed = factory.dashed();
        dashed.setId(NLId.of(UUID.randomUUID()));
        NLPrincipal dashedPrincipal = new NLPrincipal(dotted.map().getId(), dotted.getEmail(), dotted.getFirstName());
        NLPrincipal dottedPrincipal = new NLPrincipal(dashed.map().getId(), dashed.getEmail(), dashed.getFirstName());

        Assertions.assertNotNull(dottedPrincipal);
        Assertions.assertEquals(dottedPrincipal, new NLPrincipal(dashed.map().getId(), dashed.getEmail(), dashed.getFirstName()));
        Assertions.assertEquals(dottedPrincipal.toString(), dottedPrincipal.toString());
        Assertions.assertEquals(dottedPrincipal.hashCode(), dottedPrincipal.hashCode());
        Assertions.assertNotEquals(dottedPrincipal.getId(), dashedPrincipal.getId());
        Assertions.assertNotEquals(dottedPrincipal.getEmail(), dashedPrincipal.getEmail());
        Assertions.assertNotEquals(dottedPrincipal.getName(), dashedPrincipal.getName());
        Assertions.assertNotEquals(dottedPrincipal.getEmail().getValue(), dashedPrincipal.getName());
        Assertions.assertNotEquals(dottedPrincipal.toString(), dashedPrincipal.toString());
        Assertions.assertNotEquals(dottedPrincipal.hashCode(), dashedPrincipal.hashCode());
        Assertions.assertNotEquals(dottedPrincipal, dashedPrincipal);

        Assertions.assertThrows(NotImplementedException.class, () -> dashedPrincipal.implies(new Subject(true, new HashSet<>(), new HashSet<>(), new HashSet<>())));
    }


    @Test
    void shouldCreateAndCompareNLCredentials() {
        NLUser dotted = factory.dotted();
        dotted.setId(NLId.of(UUID.randomUUID()));
        NLUser dashed = factory.dashed();
        dashed.setId(NLId.of(UUID.randomUUID()));
        NLCredentials dashedCredentials = new NLCredentials(dashed.getNLPassword());
        NLCredentials dottedCredentials = new NLCredentials(dotted.getNLPassword());

        Assertions.assertNotNull(dottedCredentials);
        Assertions.assertEquals(dottedCredentials, new NLCredentials(dotted.getNLPassword()));
        Assertions.assertEquals(dottedCredentials.getPassword(), dotted.getNLPassword());
        Assertions.assertEquals(dottedCredentials.toString(), dottedCredentials.toString());
        Assertions.assertEquals(dottedCredentials.hashCode(), dottedCredentials.hashCode());
        Assertions.assertNotEquals(dottedCredentials.getPassword(), dashedCredentials.getPassword());
        Assertions.assertNotEquals(dottedCredentials.toString(), dashedCredentials.toString());
        Assertions.assertNotEquals(dottedCredentials.hashCode(), dashedCredentials.hashCode());
        Assertions.assertNotEquals(dottedCredentials, dashedCredentials);
    }
}
