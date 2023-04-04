package pl.newsler.components.user.usecase;

public record UserGetResponse(String uuid, String email, String name, String lastName, String smtpAccount,
                              String secretKey,
                              String appKey) {
}
