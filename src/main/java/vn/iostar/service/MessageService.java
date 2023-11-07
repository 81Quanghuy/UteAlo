package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.MessageRequest;
import vn.iostar.entity.Message;

public interface MessageService {

	void deleteAll();

	void delete(Message entity);

	void deleteById(String id);

	long count();

	Optional<Message> findById(String id);

	List<Message> findAll();

	<S extends Message> S save(S entity);

	ResponseEntity<GenericResponse> getListMessageByUserIdAndUserTokenId(String userId, String userIdToken,
			PageRequest pageable);

	ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserTokenId(String groupId, String userIdToken,
			PageRequest pageable);

	ResponseEntity<GenericResponse> deleteMessage(String userIdToken, MessageRequest messageRequest);

}
