package pl.newsler.commons.models;

public enum NLIdType {
    MAIL("mai"),
    CONFIRMATION_TOKEN("cot");

    private final String prefix;

    NLIdType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
