package pl.newsler.components.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import pl.newsler.commons.models.Email;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class NLUserConfiguration {
    @Bean
    NLIUserRepository employeeRepository() {
        return new NLUserRepository() {
            @Override
            public Optional<NLUser> findByEmail(Email email) {
                return Optional.empty();
            }

            @Override
            public void enableUser(Email email) {

            }

            @Override
            public List<NLUser> findAll() {
                return null;
            }

            @Override
            public List<NLUser> findAll(Sort sort) {
                return null;
            }

            @Override
            public List<NLUser> findAllById(Iterable<NLId> nlIds) {
                return null;
            }

            @Override
            public <S extends NLUser> List<S> saveAll(Iterable<S> entities) {
                return null;
            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends NLUser> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends NLUser> List<S> saveAllAndFlush(Iterable<S> entities) {
                return null;
            }

            @Override
            public void deleteAllInBatch(Iterable<NLUser> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<NLId> nlIds) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public NLUser getOne(NLId nlId) {
                return null;
            }

            @Override
            public NLUser getById(NLId nlId) {
                return null;
            }

            @Override
            public NLUser getReferenceById(NLId nlId) {
                return null;
            }

            @Override
            public <S extends NLUser> List<S> findAll(Example<S> example) {
                return null;
            }

            @Override
            public <S extends NLUser> List<S> findAll(Example<S> example, Sort sort) {
                return null;
            }

            @Override
            public Page<NLUser> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends NLUser> S save(S entity) {
                return null;
            }

            @Override
            public Optional<NLUser> findById(NLId nlId) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(NLId nlId) {
                return false;
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(NLId nlId) {

            }

            @Override
            public void delete(NLUser entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends NLId> nlIds) {

            }

            @Override
            public void deleteAll(Iterable<? extends NLUser> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public <S extends NLUser> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends NLUser> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends NLUser> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends NLUser> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends NLUser, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }
        };
    }
}
