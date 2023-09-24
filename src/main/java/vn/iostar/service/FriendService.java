package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Friend;

public interface FriendService {

	void deleteAll();

	void delete(Friend entity);

	void deleteById(Integer id);

	long count();

	Optional<Friend> findById(Integer id);

	List<Friend> findAll();

	<S extends Friend> S save(S entity);

	List<String> findFriendUserIdsByUserId(String userId);

	ResponseEntity<GenericResponse> deleteFriend(String userId, String userId2);

}
