package pl.newsler.commons.models;

public enum NLType {
    USER("usr"),
    ADMIN("adn");

    private final String prefix;

    NLType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
