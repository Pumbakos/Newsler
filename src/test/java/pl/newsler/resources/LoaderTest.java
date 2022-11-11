package pl.newsler.resources;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoaderTest {
    private final MockLoader mockLoader = new MockLoader();

    @Test
    void shouldThrowNotImplementedException() {
        Assertions.assertThrows(NotImplementedException.class, mockLoader::getResource);
        Assertions.assertThrows(NotImplementedException.class, () -> mockLoader.getResource("url"));
    }
}
