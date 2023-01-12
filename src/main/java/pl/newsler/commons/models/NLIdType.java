package pl.newsler.commons.models;

public enum NLIdType {
    MAIL("mai");

    private final String prefix;

    NLIdType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
