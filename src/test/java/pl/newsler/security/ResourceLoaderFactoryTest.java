package pl.newsler.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

class ResourceLoaderFactoryTest {
    @Test
    void shouldLoadKeystoreResource() {
        Optional<InputStream> keystoreResource = ResourceLoaderFactory.getKeystoreResource();
        Assertions.assertNotNull(keystoreResource);
        Assertions.assertTrue(keystoreResource.isPresent());
        Assertions.assertDoesNotThrow(ResourceLoaderFactory::getKeystoreResource);
    }

    @Test
    void shouldLoadSaltResource() {
        Optional<InputStream> saltResource = ResourceLoaderFactory.getSaltResource();
        Assertions.assertNotNull(saltResource);
        Assertions.assertTrue(saltResource.isPresent());
        Assertions.assertDoesNotThrow(ResourceLoaderFactory::getSaltResource);
    }

    @Test
    void shouldLoadKeystorePasswordResource() {
        Optional<InputStream> keystorePasswordResource = ResourceLoaderFactory.getKeystorePasswordResource();
        Assertions.assertNotNull(keystorePasswordResource);
        Assertions.assertTrue(keystorePasswordResource.isPresent());
        Assertions.assertDoesNotThrow(ResourceLoaderFactory::getKeystorePasswordResource);
    }

    @Test
    void shouldLoadKeystoreResourceAsFile() {
        Optional<File> keystoreResourceAsFile = ResourceLoaderFactory.getKeystoreResourceAsFile();
        Assertions.assertNotNull(keystoreResourceAsFile);
        Assertions.assertTrue(keystoreResourceAsFile.isPresent());
        Assertions.assertDoesNotThrow(ResourceLoaderFactory::getKeystoreResourceAsFile);
    }
}
