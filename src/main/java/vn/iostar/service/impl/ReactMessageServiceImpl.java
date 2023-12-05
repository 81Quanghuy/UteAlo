package vn.iostar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.dto.ReactDTO;
import vn.iostar.entity.Message;
import vn.iostar.entity.ReactMessage;
import vn.iostar.entity.User;
import vn.iostar.repository.MessageRepository;
import vn.iostar.repository.ReactMessageRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.ReactMessageService;

@Service
public class ReactMessageServiceImpl implements ReactMessageService {

    @Autowired
    ReactMessageRepository reactMessageRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public <S extends ReactMessage> S save(S entity) {
        return reactMessageRepository.save(entity);
    }

    @Override
    public List<ReactMessage> findAll() {
        return reactMessageRepository.findAll();
    }

    @Override
    public Optional<ReactMessage> findById(String id) {
        return reactMessageRepository.findById(id);
    }

    @Override
    public long count() {
        return reactMessageRepository.count();
    }

    @Override
    public void deleteById(String id) {
        reactMessageRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        reactMessageRepository.deleteAll();
    }

    @Override
    @Transactional
    public ReactMessage saveReactDTO(ReactDTO react) {
        Optional<Message> message;
        if (react.getMessageId() == null) {
            message = messageRepository.findByCreateAtAndSenderUserIdAndReceiverUserIdAndContent(react.getCreateAt(),
                    react.getSenderId(), react.getReceiverId(), react.getContent());
        } else {
            message = messageRepository.findById(react.getMessageId());
        }
        if (message.isEmpty()) {
            return null;
        }
        Optional<User> user = userRepository.findById(react.getSenderId());
        if (user.isEmpty()) {
            return null;
        }
        ReactMessage entity = new ReactMessage();
        Optional<ReactMessage> reactMessage = reactMessageRepository
                .findReactMessageByMessageMessageIdAndUserUserId(message.get().getMessageId(), user.get().getUserId());
        if (reactMessage.isPresent()) {
            entity = reactMessage.get();
            if (entity.getReact().equals(react.getReact())) {
                deleteById(entity.getReactId());
                return null;
            }
        } else {
            entity.setMessage(message.get());
            entity.setUser(user.get());
            entity.setCreateAt(new Date());
        }
        entity.setReact(react.getReact());
        entity.setUpdateAt(new Date());

        reactMessageRepository.save(entity);
        return entity;

    }

}
