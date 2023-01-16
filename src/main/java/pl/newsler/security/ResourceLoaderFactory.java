package pl.newsler.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ResourceLoaderFactory {
    private static final KeystoreLoader KEYSTORE_LOADER = new KeystoreLoader();
    private static final SaltLoader SALT_LOADER = new SaltLoader();
    private static final KeystorePasswordLoader KEYSTORE_PASSWORD_LOADER = new KeystorePasswordLoader();

    public static Optional<InputStream> getSaltResource() {
        try {
            return Optional.ofNullable(SALT_LOADER.getResource());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Optional<InputStream> getKeystorePasswordResource() {
        try {
            return Optional.ofNullable(KEYSTORE_PASSWORD_LOADER.getResource());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Optional<InputStream> getKeystoreResource() {
        try {
            return Optional.ofNullable(KEYSTORE_LOADER.getResource());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Optional<File> getKeystoreResourceAsFile() {
        try {
            return Optional.ofNullable(KEYSTORE_LOADER.getResourceAsFile());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
