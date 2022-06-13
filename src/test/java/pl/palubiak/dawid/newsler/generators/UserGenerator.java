package pl.palubiak.dawid.newsler.generators;

import org.junit.jupiter.api.TestFactory;
import pl.palubiak.dawid.newsler.user.model.User;
import pl.palubiak.dawid.newsler.user.model.requestmodel.RequestUser;

public class UserGenerator {
    @TestFactory
    public static User createUser(){
        User u = new User();
        u.setEmail("dave@aizholat.com");
        u.setName("Dave");
        u.setLastName("Aizholat");
        u.setPassword("jsad192913as");
        u.setSmtpAccount("1.newsler.com");
        u.setAppKey("asnf13f89nf10nf1f1ffaodgm02");
        u.setSecretKey("js79fh17hf4h1f94h01hf");
        u.setBusinessClients(BusinessClientGenerator.createBusinessClientList());

        return u;
    }

    @TestFactory
    public static RequestUser createSimpleUserModel() {
        return new RequestUser("dave@aizholat.com", "Dave", "jsad192913as");
    }

    @TestFactory
    public static User createNewbieUser(){
        User u = new User();
        u.setName("Dave");
        u.setEmail("dave@aizholat.com");
        u.setPassword("jsad192913as");

        return u;
    }

    @TestFactory
    public static User createBlankUser() {
        return new User();
    }

    public static RequestUser createInvalidSimpleUserModel() {
        return new RequestUser("","", "");
    }
}
