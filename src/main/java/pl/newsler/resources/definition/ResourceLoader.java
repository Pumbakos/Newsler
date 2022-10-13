package pl.newsler.resources.definition;

import java.io.FileNotFoundException;
import java.io.InputStream;

interface ResourceLoader {
    InputStream getResource(String url) throws FileNotFoundException;
}
