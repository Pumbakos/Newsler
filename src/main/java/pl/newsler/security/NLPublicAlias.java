package pl.newsler.security;

public enum NLPublicAlias {
    PE_PASSWORD(new byte[]{0, 12, 3});

    private final byte[] name;

    NLPublicAlias(byte[] name) {
        this.name = name;
    }

    public String getName() {
        return new String(name);
    }
}
