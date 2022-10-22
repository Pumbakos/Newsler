package pl.newsler.resources;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Optional;

@Component
@ApplicationScope
public class ResourceLoaderFactory {
    private static final KeystoreLoader keystoreLoader = new KeystoreLoader();

    public static Optional<InputStream> getKeystoreResource() {
        try {
            return Optional.ofNullable(keystoreLoader.getResource());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Optional<File> getKeystoreResourceAsFile() {
        try {
            return Optional.ofNullable(keystoreLoader.getResourceAsFile());
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }
    }
}
