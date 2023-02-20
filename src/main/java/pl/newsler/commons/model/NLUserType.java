package pl.newsler.commons.model;

public enum NLUserType {
    USER("usr"),
    RECEIVER("rec"),
    ADMIN("adn");

    private final String prefix;

    NLUserType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
