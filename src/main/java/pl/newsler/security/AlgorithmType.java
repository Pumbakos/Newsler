package pl.newsler.security;

public enum AlgorithmType {
    AES("AES"),
    TRIPLE_DES("DESede"),
    PBE_WITH_HMAC_SHA256_AND_AES256("PBEWithHmacSHA256AndAES_256"),
    PBE_WITH_HMAC_SHA256_AND_AES128("PBEWithHmacSHA256AndAES_128");

    private final String name;

    AlgorithmType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
