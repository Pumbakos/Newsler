package pl.newsler.components.user.dto;

public record UserUpdateRequest(String id, String appKey, String secretKey, String smtpAccount) {
}
