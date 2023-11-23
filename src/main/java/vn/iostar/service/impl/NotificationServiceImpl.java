package vn.iostar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.iostar.entity.Notification;
import vn.iostar.repository.NotificationRepository;
import vn.iostar.service.NotificationService;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void flush() {
        notificationRepository.flush();
    }

    @Override
    public <S extends Notification> S saveAndFlush(S entity) {
        return notificationRepository.saveAndFlush(entity);
    }

    @Override
    public <S extends Notification> List<S> saveAllAndFlush(Iterable<S> entities) {
        return notificationRepository.saveAllAndFlush(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<Notification> entities) {
        notificationRepository.deleteAllInBatch(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        notificationRepository.deleteAllByIdInBatch(strings);
    }

    @Override
    public void deleteAllInBatch() {
        notificationRepository.deleteAllInBatch();
    }

    @Override
    public Notification getReferenceById(String s) {
        return notificationRepository.getReferenceById(s);
    }

    @Override
    public <S extends Notification> List<S> findAll(Example<S> example) {
        return notificationRepository.findAll(example);
    }

    @Override
    public <S extends Notification> List<S> findAll(Example<S> example, Sort sort) {
        return notificationRepository.findAll(example, sort);
    }

    @Override
    public <S extends Notification> List<S> saveAll(Iterable<S> entities) {
        return notificationRepository.saveAll(entities);
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findAllById(Iterable<String> strings) {
        return notificationRepository.findAllById(strings);
    }

    @Override
    public <S extends Notification> S save(S entity) {
        return notificationRepository.save(entity);
    }

    @Override
    public Optional<Notification> findById(String s) {
        return notificationRepository.findById(s);
    }

    @Override
    public long count() {
        return notificationRepository.count();
    }

    @Override
    public void deleteById(String s) {
        notificationRepository.deleteById(s);
    }

    @Override
    public void delete(Notification entity) {
        notificationRepository.delete(entity);
    }

    @Override
    public void deleteAll() {
        notificationRepository.deleteAll();
    }

    @Override
    public List<Notification> findAll(Sort sort) {
        return notificationRepository.findAll(sort);
    }

    @Override
    public Page<Notification> findAll(Pageable pageable) {
        return notificationRepository.findAll(pageable);
    }

    @Override
    public <S extends Notification> Page<S> findAll(Example<S> example, Pageable pageable) {
        return notificationRepository.findAll(example, pageable);
    }

    @Override
    public <S extends Notification> long count(Example<S> example) {
        return notificationRepository.count(example);
    }
}
