package pl.newsler.testcommons.environment;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import pl.newsler.testcommons.PleaseImplementMeException;

import java.util.HashMap;
import java.util.Map;

public class StubEnvironment implements Environment {
    private final Map<String, String> props = new HashMap<>();

    public StubEnvironment(StubEnvironmentCreationStrategy strategy) {
        strategy.create(props);
    }

    @Override
    public String @NotNull [] getActiveProfiles() {
        throw new PleaseImplementMeException();
    }

    @Override
    public String @NotNull [] getDefaultProfiles() {
        throw new PleaseImplementMeException();
    }

    @Override
    public boolean acceptsProfiles(final String @NotNull ... profiles) {
        throw new PleaseImplementMeException();
    }

    @Override
    public boolean acceptsProfiles(final @NotNull Profiles profiles) {
        throw new PleaseImplementMeException();
    }

    @Override
    public boolean containsProperty(final @NotNull String key) {
        throw new PleaseImplementMeException();
    }

    @Override
    public String getProperty(final @NotNull String key) {
        return props.get(key);
    }

    @Override
    public @NotNull String getProperty(final @NotNull String key, final @NotNull String defaultValue) {
        return props.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T getProperty(final @NotNull String key, final @NotNull Class<T> targetType) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <T> @NotNull T getProperty(final @NotNull String key, final @NotNull Class<T> targetType, final @NotNull T defaultValue) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull String getRequiredProperty(final @NotNull String key) throws IllegalStateException {
        throw new PleaseImplementMeException();
    }

    @Override
    public <T> @NotNull T getRequiredProperty(final @NotNull String key, final @NotNull Class<T> targetType) throws IllegalStateException {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull String resolvePlaceholders(final @NotNull String text) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull String resolveRequiredPlaceholders(final @NotNull String text) throws IllegalArgumentException {
        throw new PleaseImplementMeException();
    }
}
