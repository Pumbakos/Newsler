package pl.newsler.components.signup.usecase;

public record UserCreateRequest(String name, String lastName, String email, String password) {
}
