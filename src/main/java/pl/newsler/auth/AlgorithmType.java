package pl.newsler.auth;

public enum AlgorithmType {
    AES("AES");

    private final String name;

    AlgorithmType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
