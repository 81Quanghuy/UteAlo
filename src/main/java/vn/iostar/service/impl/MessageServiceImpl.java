package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Message;
import vn.iostar.entity.User;
import vn.iostar.repository.MessageRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public <S extends Message> S save(S entity) {
		return messageRepository.save(entity);
	}

	@Override
	public List<Message> findAll() {
		return messageRepository.findAll();
	}

	@Override
	public Optional<Message> findById(String id) {
		return messageRepository.findById(id);
	}

	@Override
	public long count() {
		return messageRepository.count();
	}

	@Override
	public void deleteById(String id) {
		messageRepository.deleteById(id);
	}

	@Override
	public void delete(Message entity) {
		messageRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		messageRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getListMessageByUserIdAndUserTokenId(@Valid String userId,
			String userIdToken) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, userIdToken);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully").result(messages)
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserTokenId(@Valid String groupId,
			String userIdToken) {
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		List<Message> messages = messageRepository.findByGroupIdOrderByCreateAt(groupId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully").result(messages)
				.statusCode(HttpStatus.OK.value()).build());
	}

}
