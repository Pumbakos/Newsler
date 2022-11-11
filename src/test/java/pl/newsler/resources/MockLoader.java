package pl.newsler.resources;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MockLoader implements Loader {

    @Override
    public InputStream getResource() throws FileNotFoundException {
        return Loader.super.getResource();
    }

    @Override
    public InputStream getResource(String url) throws FileNotFoundException {
        return Loader.super.getResource(url);
    }
}
