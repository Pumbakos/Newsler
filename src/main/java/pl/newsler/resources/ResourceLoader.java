package pl.newsler.resources;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface ResourceLoader {
    InputStream getResource(String url) throws FileNotFoundException;
}
