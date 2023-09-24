package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.FriendRequest;

public interface FriendRequestService {

	void deleteAll();

	void deleteById(Integer id);
	
	long count();

	Optional<FriendRequest> findById(Integer id);

	List<FriendRequest> findAll();

	<S extends FriendRequest> S save(S entity);

	List<String> findUserFromUserIdByUserToUserId(String userId);
	
	ResponseEntity<GenericResponse> deleteFriendRequest (String userFromId, String userToId);
	
	ResponseEntity<GenericResponse> acceptRequest (String userFromId, String userToId);

}
