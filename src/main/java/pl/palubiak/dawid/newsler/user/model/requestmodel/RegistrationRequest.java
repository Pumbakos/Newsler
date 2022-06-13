package pl.palubiak.dawid.newsler.user.model.requestmodel;

public record RegistrationRequest(String name, String lastName, String password, String email) {
}
