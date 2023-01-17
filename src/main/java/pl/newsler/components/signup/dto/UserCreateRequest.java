package pl.newsler.components.signup.dto;

public record UserCreateRequest(String name, String lastName, String email, String password) {
}
