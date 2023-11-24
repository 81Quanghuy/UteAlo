package vn.iostar.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.MessageDTO;
import vn.iostar.dto.MessageRequest;
import vn.iostar.entity.FilesMedia;
import vn.iostar.entity.Message;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;
import vn.iostar.repository.FileRepository;
import vn.iostar.repository.MessageRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.MessageService;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Path;
@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	static Cloudinary cloudinary;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostGroupRepository postGroupRepository;

	@Autowired
	private FileRepository fileRepository;

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
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, userIdToken, pageable);
		List<MessageDTO> list = new ArrayList<>();
		for (Message message : messages) {
			list.add(new MessageDTO(message));
		}

		Collections.reverse(messages);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get MessageDTO successfully").result(list)
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserTokenId(String groupId, String userIdToken,
			PageRequest pageable) {
		Optional<User> userToken = userRepository.findById(userIdToken);
		if (userToken.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		List<Message> messages = messageRepository.findByGroupPostGroupIdOrderByCreateAt(Integer.parseInt(groupId), pageable);
		Collections.reverse(messages);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully").result(messages)
				.statusCode(HttpStatus.OK.value()).build());
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

        Message entity = new Message();
        entity.setContent(message.getContent());
		if	(message.getSenderId() != null)
		{Optional<User> sender = userRepository.findById(message.getSenderId());
        sender.ifPresent(entity::setSender);}
		if	(message.getReceiverId() != null){
        Optional<User> receiver = userRepository.findById(message.getReceiverId());
        receiver.ifPresent(entity::setReceiver);}

		if(message.getGroupId() != null){
			Optional<PostGroup> group = postGroupRepository.findById(Integer.valueOf(message.getGroupId()));
			group.ifPresent(entity::setGroup);
		}
		entity.setCreateAt(message.getCreatedAt());
		entity.setUpdateAt(message.getUpdatedAt());

		if (!message.getFileEntities().isEmpty()){
			List<FilesMedia> listFiles = new ArrayList<>();
			for (FilesMedia file : message.getFileEntities()) {
				FilesMedia file1 = new FilesMedia();
				file1.setName(file.getName());
				file1.setType(file.getType());
				file1.setType(file.getType());
				file1.setCreateAt(new Date());
				file1.setUpdateAt(new Date());
				file1.setUrl(getBlobUrlContent(file.getUrl(), file.getName()));
				file1.setMessage(entity);
				listFiles.add(file1);
			}
			entity.setFiles(listFiles);
			fileRepository.saveAll(listFiles);
		}
			save(entity);

        return entity;
    }
	private String getBlobUrlContent(String blobUrl,String originalFileName) {
		// Đọc nội dung từ URL blob
		try {
			URL url = new URL(blobUrl);
			byte[] bytes = Files.readAllBytes(Path.of(url.toURI()));
			String publicId = "Social Media/User/" + originalFileName; // Sử dụng tên gốc làm public_id

			Map params = ObjectUtils.asMap("public_id", publicId, "resource_type", "auto");
			Map uploadResult = cloudinary.uploader().upload(bytes, params);
			return (String) uploadResult.get("secure_url");
		} catch (Exception e) {
			throw new RuntimeException("Error reading blob content", e);
		}
	}
}
