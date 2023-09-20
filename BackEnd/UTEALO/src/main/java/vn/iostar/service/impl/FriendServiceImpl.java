package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Friend;
import vn.iostar.entity.User;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.FriendService;

@Service
public class FriendServiceImpl implements FriendService {

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public List<String> findFriendUserIdsByUserId(String userId) {
		return friendRepository.findFriendUserIdsByUserId(userId);
	}

	@Override
	public <S extends Friend> S save(S entity) {
		return friendRepository.save(entity);
	}

	@Override
	public List<Friend> findAll() {
		return friendRepository.findAll();
	}

	@Override
	public Optional<Friend> findById(Integer id) {
		return friendRepository.findById(id);
	}

	@Override
	public long count() {
		return friendRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		friendRepository.deleteById(id);
	}

	@Override
	public void delete(Friend entity) {
		friendRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		friendRepository.deleteAll();
	}

	public Friend getFriendByUser1AndUser2(String userId, String userId2) {

		Friend friend1 = friendRepository.findByUser1UserIdAndUser2UserId(userId, userId2);
		if (friend1 != null) {
			return friend1;
		}

		return friendRepository.findByUser1UserIdAndUser2UserId(userId2, userId);
	}

	@Override
	public ResponseEntity<GenericResponse> deleteFriend(String userId, String userId2) {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}

		Friend friend = getFriendByUser1AndUser2(userId, userId2);
		if (friend != null) {
			friendRepository.delete(friend);
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		}
		return ResponseEntity.ok().body(new GenericResponse(true, "Both Not Friend", null, HttpStatus.OK.value()));
	}

}
