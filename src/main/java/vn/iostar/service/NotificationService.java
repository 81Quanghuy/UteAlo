package vn.iostar.service;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import vn.iostar.entity.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    void flush();

    <S extends Notification> S saveAndFlush(S entity);

    <S extends Notification> List<S> saveAllAndFlush(Iterable<S> entities);

    void deleteAllInBatch(Iterable<Notification> entities);

    void deleteAllByIdInBatch(Iterable<String> strings);

    void deleteAllInBatch();

    Notification getReferenceById(String s);

    <S extends Notification> List<S> findAll(Example<S> example);

    <S extends Notification> List<S> findAll(Example<S> example, Sort sort);

    <S extends Notification> List<S> saveAll(Iterable<S> entities);

    List<Notification> findAll();

    List<Notification> findAllById(Iterable<String> strings);

    <S extends Notification> S save(S entity);

    Optional<Notification> findById(String s);

    long count();

    void deleteById(String s);

    void delete(Notification entity);

    void deleteAll();

    List<Notification> findAll(Sort sort);

    Page<Notification> findAll(Pageable pageable);

    <S extends Notification> Page<S> findAll(Example<S> example, Pageable pageable);

    <S extends Notification> long count(Example<S> example);
}
