package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.FriendResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Friend;
import vn.iostar.entity.FriendRequest;
import vn.iostar.entity.User;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.FriendRequestRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.FriendService;

@Service
public class FriendServiceImpl implements FriendService {

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Override
	public List<FriendResponse> findFriendUserIdsByUserId(String userId) {
		
		return friendRepository.findFriendUserIdsByUserId(userId);
	}
	@Override
	public List<FriendResponse> findFriendByUserId(String userId, Pageable pageable) {
		return friendRepository.findFriendByUserId(userId, pageable);
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

	@Override
	public ResponseEntity<GenericResponse> deleteFriend(String userId, String userId2) {
		Optional<User> user = userRepository.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}

		Optional<Friend> friend = friendRepository.findByUser1UserIdAndUser2UserId(userId, userId2);
		Optional<Friend> friend1 = friendRepository.findByUser1UserIdAndUser2UserId(userId2, userId);
		if (friend.isPresent()) {
			friendRepository.delete(friend.get());
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		} else if (friend1.isPresent()) {
			friendRepository.delete(friend1.get());
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		} else
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot delete!", null, HttpStatus.NOT_FOUND.value()));
	}

	@Override
	public ResponseEntity<GenericResponse> getStatusByUserId(String userId, String userIdToken) {
		Optional<Friend> friend = friendRepository.findByUser1UserIdAndUser2UserId(userId, userIdToken);
		Optional<Friend> friend2 = friendRepository.findByUser1UserIdAndUser2UserId(userIdToken, userId);
		if (friend.isPresent() || friend2.isPresent()) {
			return ResponseEntity.ok().body(new GenericResponse(true, "Bạn bè", "null", HttpStatus.OK.value()));
		}
		Optional<FriendRequest> friendRequest = friendRequestRepository.findByUserFromUserIdAndUserToUserId(userId,
				userIdToken);
		if (friendRequest.isPresent()) {
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Chấp nhận lời mời", "null", HttpStatus.OK.value()));
		}
		Optional<FriendRequest> friendRequest1 = friendRequestRepository
				.findByUserFromUserIdAndUserToUserId(userIdToken, userId);
		if (friendRequest1.isPresent()) {
			return ResponseEntity.ok().body(new GenericResponse(true, "Đã gửi lời mời", "null", HttpStatus.OK.value()));
		}
		return ResponseEntity.ok().body(new GenericResponse(true, "Kết bạn", "null", HttpStatus.OK.value()));
	}

}
