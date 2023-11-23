package vn.iostar.service.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.MessageDTO;
import vn.iostar.dto.MessageRequest;
import vn.iostar.entity.Files;
import vn.iostar.entity.Message;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;
import vn.iostar.repository.MediaRepository;
import vn.iostar.repository.MessageRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.MessageService;

@Service
public class MessageServiceImpl implements MessageService {

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private CloudinaryService cloudinaryService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PostGroupRepository postGroupRepository;

	@Autowired
	private MediaRepository mediaRepository;

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

        if (message.getFiles()!=null ) {
			List<Files> mediaList = new ArrayList<>();
            for (MultipartFile file : message.getFiles()) {
                Files vi = new Files();
                //check file is image
                if (Objects.requireNonNull(file.getContentType()).contains("image")) {
                    vi.setName(file.getOriginalFilename());
                    vi.setUrl(cloudinaryService.uploadImage(file));
                    vi.setType(file.getContentType());
                    vi.setCreateAt(new Timestamp(System.currentTimeMillis()));
                    vi.setUpdateAt(new Timestamp(System.currentTimeMillis()));

                    mediaList.add(vi);
                }
                //check file is video
                else if ("video".contains(file.getContentType())) {

                    vi.setName(file.getOriginalFilename());
                    vi.setUrl(cloudinaryService.uploadVideo(file));
                    vi.setType(file.getContentType());
                    vi.setCreateAt(new Timestamp(System.currentTimeMillis()));
                    vi.setUpdateAt(new Timestamp(System.currentTimeMillis()));
                    mediaList.add(vi);
                } else {
                    vi.setName(file.getOriginalFilename());
                    vi.setUrl(cloudinaryService.uploadFile(file));
                    vi.setType(file.getContentType());
                    vi.setCreateAt(new Timestamp(System.currentTimeMillis()));
                    vi.setUpdateAt(new Timestamp(System.currentTimeMillis()));
                    mediaList.add(vi);
                }
            }
			entity.setFiles(mediaRepository.saveAll(mediaList));
        }

        // Lưu tin nhắn vào cơ sở dữ liệu
		entity.setCreateAt(message.getCreatedAt());
		entity.setUpdateAt(message.getUpdatedAt());
        save(entity);

        return entity;
    }

}
