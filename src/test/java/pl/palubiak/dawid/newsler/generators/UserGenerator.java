package pl.palubiak.dawid.newsler.generators;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.TestFactory;
import pl.palubiak.dawid.newsler.user.model.User;

import java.util.List;

public class UserGenerator {
    @TestFactory
    public static User createUser(){
        User u = new User();
        u.setId(1L);
        u.setEmail("dave@aizholat.com");
        u.setName("Dave");
        u.setLastName("Aizholat");
        u.setPassword("jsad192913as");
        u.setSmtpAccount("1.newsler.com");
        u.setAPP_KEY("asnf13f89nf10nf1f1ffaodgm02");
        u.setSECRET_KEY("js79fh17hf4h1f94h01hf");
        u.setBusinessClients(List.of());

        return u;
    }
}
