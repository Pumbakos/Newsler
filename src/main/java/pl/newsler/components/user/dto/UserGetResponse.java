package pl.newsler.components.user.dto;

public record UserGetResponse(String uuid, String email, String name, String lastName, String smtpAccount, String secretKey,
                              String appKey) {
}
