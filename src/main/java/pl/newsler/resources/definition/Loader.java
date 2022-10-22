package pl.newsler.resources.definition;

import org.apache.commons.lang3.NotImplementedException;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface Loader {
    @SuppressWarnings({"unused"})
    default InputStream getResource() throws FileNotFoundException {
        throw new NotImplementedException(String.format("Do not use %s explicitly, use one of child that overrides method", Loader.class.getName()));
    }

    @SuppressWarnings({"unused"})
    default InputStream getResource(String url) throws FileNotFoundException {
        throw new NotImplementedException(String.format("Do not use %s explicitly, use one of child that overrides method", Loader.class.getName()));
    }
}
