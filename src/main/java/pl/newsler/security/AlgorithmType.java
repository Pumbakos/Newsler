package pl.newsler.security;

public enum AlgorithmType {
    AES("AES"),
    PBE_WITH_HMAC_SHA256_AND_AES256("PBEWithHmacSHA256AndAES_256"),
    RSA("RSA");

    private final String name;

    AlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
