package pl.newsler.security;

public enum NLPublicAlias {
    PE_PASSWORD(new byte[]{80, 69, 95, 80, 65, 83, 83, 87, 79, 82, 68});

    private final byte[] name;

    NLPublicAlias(byte[] name) {
        this.name = name;
    }

    public String getName() {
        return new String(name);
    }
}
