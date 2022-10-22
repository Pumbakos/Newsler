package pl.newsler.auth;

public record UserAuthModel(String email, String password, String smtpAccount, String appKey){}