package vn.iostar.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.MessageDTO;
import vn.iostar.dto.MessageRequest;
import vn.iostar.entity.Message;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.ReactMessage;
import vn.iostar.entity.User;
import vn.iostar.repository.MessageRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.MessageService;
import vn.iostar.service.ReactMessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostGroupRepository postGroupRepository;

	@Autowired
	private Cloudinary cloudinary;

	@Autowired
	private ReactMessageService reactMessageService;

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
	public ResponseEntity<GenericResponse> getListMessageByUserIdAndUserTokenId(String userId, String userIdToken,
			PageRequest pageable) {
		Optional<User> user = userRepository.findById(userId);
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (user.isEmpty() || userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, userIdToken, pageable);

		List<MessageDTO> messageDTO = new ArrayList<>();
		for (Message entity : messages) {
			messageDTO.add(new MessageDTO(entity));
		}

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get MessageDTO successfully")
				.result(messageDTO).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserTokenId(String groupId, PageRequest pageable) {
		Optional<PostGroup> group = postGroupRepository.findById(Integer.parseInt(groupId));
		if (group.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<Message> messages = messageRepository.findByGroupPostGroupIdOrderByCreateAt(Integer.parseInt(groupId),
				pageable);
		List<MessageDTO> messageDTO = new ArrayList<>();
		for (Message entity : messages) {
			messageDTO.add(new MessageDTO(entity));
		}

		if (messages.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found message").statusCode(HttpStatus.NOT_FOUND.value()).build());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get list Message successfully")
				.result(messageDTO).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deleteMessage(String userIdToken, MessageRequest messageRequest) {
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Timestamp timestamp = Timestamp.valueOf(messageRequest.getCreateAt());
		Optional<Message> entity = messageRepository.findByCreateAtAndSenderUserIdAndReceiverUserIdAndContent(timestamp,
				messageRequest.getSenderId(), messageRequest.getReceiverId(), messageRequest.getContent());
		if (entity.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found message").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		messageRepository.delete(entity.get());
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	@Transactional
	public Message saveMessageByDTO(MessageDTO message) throws IOException {

		Optional<Message> message1 = messageRepository.findByCreateAtAndSenderUserIdAndReceiverUserIdAndContent(
				message.getCreatedAt(), message.getSenderId(), message.getReceiverId(), message.getContent());
		Message entity;
		entity = message1.orElseGet(Message::new);
		entity.setContent(message.getContent());
		if (message.getSenderId() != null) {
			Optional<User> sender = userRepository.findById(message.getSenderId());
			sender.ifPresent(entity::setSender);
		}
		if (message.getReceiverId() != null) {
			Optional<User> receiver = userRepository.findById(message.getReceiverId());
			receiver.ifPresent(entity::setReceiver);
		}

		if (message.getGroupId() != null && (!message.getGroupId().equals("null"))) {
			Optional<PostGroup> group = postGroupRepository.findById(Integer.valueOf(message.getGroupId()));
			group.ifPresent(entity::setGroup);

		}
		entity.setCreateAt(message.getCreatedAt());
		entity.setUpdateAt(message.getUpdatedAt());
		entity.setIsDeleted(message.getIsDeleted());
		if (message.getFiles() != null) {
			String publicId = "Social Media/User/" + "FileM"; // Sử dụng tên gốc làm public_id

			Map<String, String> params = ObjectUtils.asMap("public_id", publicId, "resource_type", "auto");
			Map uploadResult = cloudinary.uploader().upload(message.getFiles(), params);
			entity.setFiles((String) uploadResult.get("secure_url"));
		}
		save(entity);
		return entity;
	}

	@Override
	public ResponseEntity<GenericResponse> getListReactInMessage(Date createAt, PageRequest pageable) {
		List<ReactMessage> list = reactMessageService.findReactMessageByCreateAt(createAt);

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("get list successfully").result(list)
				.statusCode(HttpStatus.OK.value()).build());
	}
}
