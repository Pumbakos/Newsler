package pl.newsler.testcommons;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
public class InMemoryJpaRepository<T, ID> implements JpaRepository<T, ID> {
    private final Function<T, ID> idFunction;

    protected final Map<ID, T> database = new HashMap<>();

    @Override
    public @NotNull List<T> findAll() {
        return database.values().stream().toList();
    }

    @Override
    public @NotNull List<T> findAll(@NotNull Sort sort) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull Page<T> findAll(@NotNull Pageable pageable) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull List<T> findAllById(@NotNull Iterable<ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public long count() {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteById(@NotNull ID id) {
        database.remove(id);
    }

    @Override
    public void delete(@NotNull T entity) {
        database.remove(idFunction.apply(entity), entity);
    }

    @Override
    public void deleteAllById(@NotNull Iterable<? extends ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAll(@NotNull Iterable<? extends T> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAll() {
        database.clear();
    }

    @Override
    public <S extends T> @NotNull S save(@NotNull S entity) {
        ID id = idFunction.apply(entity);
        database.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> @NotNull List<S> saveAll(Iterable<S> entities) {
        final List<S> list = new ArrayList<>();
        for (final S entity : entities) {
            list.add(save(entity));
        }

        return list;
    }

    @Override
    public @NotNull Optional<T> findById(@NotNull ID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public boolean existsById(@NotNull ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void flush() {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull S saveAndFlush(@NotNull S entity) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull List<S> saveAllAndFlush(@NotNull Iterable<S> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllInBatch(@NotNull Iterable<T> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllByIdInBatch(@NotNull Iterable<ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull T getOne(@NotNull ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull T getById(@NotNull ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public @NotNull T getReferenceById(@NotNull ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull Optional<S> findOne(@NotNull Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull List<S> findAll(@NotNull Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull List<S> findAll(@NotNull Example<S> example, @NotNull Sort sort) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> @NotNull Page<S> findAll(@NotNull Example<S> example, @NotNull Pageable pageable) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> long count(@NotNull Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> boolean exists(@NotNull Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T, R> @NotNull R findBy(@NotNull Example<S> example, @NotNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new PleaseImplementMeException();
    }
}
