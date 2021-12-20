package pl.palubiak.dawid.newsler.generators;

import org.junit.jupiter.api.TestFactory;
import pl.palubiak.dawid.newsler.businesclinet.model.BusinessClient;
import pl.palubiak.dawid.newsler.businesclinet.model.EmailType;
import pl.palubiak.dawid.newsler.user.model.User;

import java.util.List;

public class BusinessClientGenerator {
    @TestFactory
    public static List<BusinessClient> createBusinessClientList(){
        return List.of(createBusinessClient(), createBusinessClient());
    }

    @TestFactory
    public static BusinessClient createBusinessClient(){
        BusinessClient b = new BusinessClient();
        b.setId(1L);
        b.setEmail("dave@aizholat.com");
        b.setName("Dave");
        b.setLastName("Pumbakos");
        b.setUser(new User());
        b.setEmailType(EmailType.ALL);

        return b;
    }

    @TestFactory
    public static BusinessClient createBusinessClientWithNullId(){
        BusinessClient b = new BusinessClient();
        b.setEmail("dave@aizholat.com");
        b.setName("Dave");
        b.setLastName("Pumbakos");
        b.setUser(new User());
        b.setEmailType(EmailType.NEWSLETTER);

        return b;
    }

    @TestFactory
    public static BusinessClient createInvalidBusinessClient(){
        return new BusinessClient();
    }
}
