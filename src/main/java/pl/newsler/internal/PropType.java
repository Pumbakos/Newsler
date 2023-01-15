package pl.newsler.internal;

enum PropType {
    SERVER_SSL_KEYSTORE_FILE("server.ssl.key-store"),
    SERVER_SSL_KEYSTORE_PASSWORD("server.ssl.key-store-password"),
    SERVER_SSL_KEYSTORE_TYPE("server.ssl.key-store-type"),
    SERVER_SSL_KEYSTORE_ALIAS("server.ssl.key-alias"),
    SERVER_SSL_ENABLED("server.ssl.enabled"),
    SERVER_SSL_PORT("server.ssl.port"),
    NEWSLER_SSL_KEYSTORE_FILE("newsler.ssl.keystore.file"),
    NEWSLER_SSL_KEYSTORE_PASSWORD("newsler.ssl.keystore.password"),
    NEWSLER_SSL_KEYSTORE_TYPE("newsler.ssl.keystore.type"),
    NEWSLER_SSL_KEYSTORE_ALIAS("newsler.ssl.keystore.alias");

    private final String value;

    PropType(final String value) {
        this.value = value;
    }

    String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
