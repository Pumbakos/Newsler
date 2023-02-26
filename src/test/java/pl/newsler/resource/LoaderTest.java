package pl.newsler.resource;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LoaderTest {
    private final StubLoader stubLoader = new StubLoader();

    @Test
    void shouldThrowNotImplementedException() {
        Assertions.assertThrows(NotImplementedException.class, stubLoader::getResource);
        Assertions.assertThrows(NotImplementedException.class, () -> stubLoader.getResource("url"));
    }
}
