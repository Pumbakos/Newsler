package pl.newsler.security;

public enum NLAlias {
    SMTP_ACCOUNT(new byte[]{78, 69, 87, 83, 76, 69, 82, 95, 83, 77, 84, 80}),
    APP_KEY(new byte[]{78, 69, 87, 83, 76, 69, 82, 95, 65, 80, 80, 95, 75, 69, 89}),
    SECRET_KEY(new byte[]{78, 69, 87, 83, 76, 69, 82, 95, 83, 69, 67, 82, 69, 84, 95, 75, 69, 89}),
    PE_SALT(new byte[]{80, 69, 95, 83, 65, 76, 84});

    private final byte[] name;

    NLAlias(byte[] name) {
        this.name = name;
    }

    public String getName() {
        return new String(name);
    }
}
