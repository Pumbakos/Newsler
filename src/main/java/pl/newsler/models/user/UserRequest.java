package pl.newsler.models.user;

public record UserRequest(
        String name,
        String lastName,
        String password,
        String email,
        String appKey,
        String secretKey,
        String smtpAccount
) {}