package pl.newsler.resource;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ResourceLoader {
    InputStream getResource(String url) throws FileNotFoundException;
}
