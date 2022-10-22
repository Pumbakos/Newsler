package pl.newsler.resources;

import org.springframework.util.ResourceUtils;
import pl.newsler.resources.definition.FixedResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class KeystoreLoader implements FixedResourceLoader {
    @Override
    public InputStream getResource() throws FileNotFoundException {
        try {
            return new FileInputStream(ResourceUtils.getFile("classpath:keystore/keystore.p12"));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File keystore.p12 not found");
        }
    }

    public File getResourceAsFile() throws FileNotFoundException {
        try {
            return ResourceUtils.getFile("classpath:keystore/keystore.p12");
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("File keystore.p12 not found");
        }
    }
}
