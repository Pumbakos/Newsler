package pl.newsler.testcommons.environment;

import java.util.Map;

public interface StubEnvironmentCreationStrategy {
    void create(Map<String, String> map);
}
