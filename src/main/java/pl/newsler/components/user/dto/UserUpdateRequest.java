package pl.newsler.components.user.dto;

public record UserUpdateRequest(String email, String appKey, String secretKey, String smtpAccount) {
}
