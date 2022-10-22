package pl.newsler.resources.definition;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FixedResourceLoader extends Loader{
    InputStream getResource() throws FileNotFoundException;
}
