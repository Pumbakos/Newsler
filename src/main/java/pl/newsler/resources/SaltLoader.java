package pl.newsler.resources;

import org.springframework.util.ResourceUtils;
import pl.newsler.resources.definition.FixedResourceLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

class SaltLoader implements FixedResourceLoader {
    @Override
    public InputStream getResource() throws FileNotFoundException {
        try {
            return new FileInputStream(ResourceUtils.getFile("classpath:keystore/salt.bin"));
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Salt file not found");
        }
    }
}