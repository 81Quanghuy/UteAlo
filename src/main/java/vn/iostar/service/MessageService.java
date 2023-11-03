package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Message;

public interface MessageService {

	void deleteAll();

	void delete(Message entity);

	void deleteById(String id);

	long count();

	Optional<Message> findById(String id);

	List<Message> findAll();

	<S extends Message> S save(S entity);

	ResponseEntity<GenericResponse> getListMessageByUserIdAndUserTokenId(@Valid String userId, String userIdToken);

	ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserTokenId(@Valid String groupId, String userIdToken);

}
