package pl.newsler.components.user.usecase;

public record UserUpdateRequest(String email, String appKey, String secretKey, String smtpAccount) {
}
