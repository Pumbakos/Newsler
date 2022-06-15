package pl.palubiak.dawid.newsler.user.model.requestmodel;

import pl.palubiak.dawid.newsler.user.model.User;

public record UserRequest(String name, String lastName, String password, String email, String appKey, String secretKey, String smtpAccount) {
    public User toDto(){
        User user = new User();
        user.setName(name);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setAppKey(appKey);
        user.setSecretKey(secretKey);
        user.setSmtpAccount(smtpAccount);

        return user;
    }
}
