package pl.newsler.testcommons;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class InMemoryJpaRepository<T, ID> implements JpaRepository<T, ID> {
    private final Function<T, ID> idFunction;
    protected final Map<ID, T> database = new HashMap<>();

    public InMemoryJpaRepository(Function<T, ID> idFunction) {
        this.idFunction = idFunction;
    }

    @Override
    public List<T> findAll() {
        throw new PleaseImplementMeException();
    }

    @Override
    public List<T> findAll(Sort sort) {
        throw new PleaseImplementMeException();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        throw new PleaseImplementMeException();
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public long count() {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteById(ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void delete(T entity) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAll() {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> S save(S entity) {
        ID id = idFunction.apply(entity);
        database.put(id, entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void flush() {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        throw new PleaseImplementMeException();
    }

    @Override
    public void deleteAllInBatch() {
        throw new PleaseImplementMeException();
    }

    @Override
    public T getOne(ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public T getById(ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public T getReferenceById(ID id) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        throw new PleaseImplementMeException();
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new PleaseImplementMeException();
    }
}
