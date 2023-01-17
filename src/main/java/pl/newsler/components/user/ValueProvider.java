package pl.newsler.components.user;

public enum ValueProvider {
    HOME_DOMAIN("http://localhost:8080"), //FIXME: set up spring prop
    CONFIRMED("CONFIRMED"), NOT_CONFIRMED("NOT_CONFIRMED"),
    VALID_EMAIL("VALID_EMAIL"), NOT_VALID_EMAIL("NOT_VALID_EMAIL"), EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS"),
    VALID_PASSWORD("VALID_PASSWORD"), NOT_VALID_PASSWORD("NOT_VALID_PASSWORD"),
    VALID_USERNAME("VALID_USERNAME"), NOT_VALID_USERNAME("NOT_VALID_USERNAME"),
    VALID_TOKEN("VALID_TOKEN"), NOT_VALID_TOKEN("NOT_VALID_TOKEN"),
    REGISTERED("REGISTERED"), NOT_REGISTERED("NOT_REGISTERED"),
    RESENT("RESENT"), NOT_SENT("NOT_SENT");

    private final String value;

    ValueProvider(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
