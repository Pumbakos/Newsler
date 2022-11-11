package pl.newsler.resources;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FixedResourceLoader extends Loader {
    @Override
    InputStream getResource() throws FileNotFoundException;
}
