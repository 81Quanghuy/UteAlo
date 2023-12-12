package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.FriendRequestDTO;
import vn.iostar.dto.FriendResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Friend;
import vn.iostar.entity.FriendRequest;
import vn.iostar.entity.User;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.FriendRequestRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.FriendRequestService;

@Service
public class FriendRequestServiceImpl implements FriendRequestService {

	@Autowired
	FriendRequestRepository friendRequestRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	FriendRepository friendRepository;

	@Override
	public List<FriendResponse> findUserFromUserIdByUserToUserId(String userId) {
		return friendRequestRepository.findUserFromUserIdByUserToUserId(userId);
	}

	@Override
	public <S extends FriendRequest> S save(S entity) {
		return friendRequestRepository.save(entity);
	}

	@Override
	public List<FriendRequest> findAll() {
		return friendRequestRepository.findAll();
	}

	@Override
	public Optional<FriendRequest> findById(Integer id) {
		return friendRequestRepository.findById(id);
	}

	@Override
	public long count() {
		return friendRequestRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		friendRequestRepository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		friendRequestRepository.deleteAll();
	}

	public Optional<FriendRequest> findByUserFromUserIdAndUserToUserId(String userFromId, String userToID) {
		return friendRequestRepository.findByUserFromUserIdAndUserToUserId(userFromId, userToID);
	}

	@Override
	public ResponseEntity<GenericResponse> deleteFriendRequest(String userFromId, String userToId) {
		Optional<User> user = userRepository.findById(userFromId);
		Optional<User> user1 = userRepository.findById(userToId);
		if (!user.isPresent() || !user1.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}

		Optional<FriendRequest> reList = findByUserFromUserIdAndUserToUserId(userFromId, userToId);
		if (reList.isPresent()) {
			deleteById(reList.get().getFriendRequestId());
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		}
		return ResponseEntity.ok()
				.body(new GenericResponse(false, "NOT FOUND FRIEND REQUEST!", null, HttpStatus.NOT_FOUND.value()));
	}

	@Override
	public ResponseEntity<GenericResponse> acceptRequest(String userFromId, String userToId) {
		Optional<User> user = userRepository.findById(userFromId);
		if (!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
		Optional<FriendRequest> reList = findByUserFromUserIdAndUserToUserId(userFromId, userToId);
		if (reList.isPresent()) {
			if (Boolean.TRUE.equals(createFriend(reList.get()))) {
				deleteById(reList.get().getFriendRequestId());

				return ResponseEntity.ok()
						.body(new GenericResponse(true, "Accept Successful!", null, HttpStatus.OK.value()));
			} else {
				return ResponseEntity.ok()
						.body(new GenericResponse(false, "Both was friend before ", null, HttpStatus.CREATED.value()));
			}

		}
		return ResponseEntity.ok()
				.body(new GenericResponse(false, "NOT FOUND FRIEND REQUEST!", null, HttpStatus.NOT_FOUND.value()));
	}

	private Boolean createFriend(FriendRequest friendRequest) {
		Optional<Friend> friend2 = friendRepository.findByUser1UserIdAndUser2UserId(
				friendRequest.getUserFrom().getUserId(), friendRequest.getUserTo().getUserId());
		Optional<Friend> friend3 = friendRepository.findByUser1UserIdAndUser2UserId(
				friendRequest.getUserTo().getUserId(), friendRequest.getUserFrom().getUserId());
		if (friend2.isEmpty() && friend3.isEmpty()) {
			Friend friend = new Friend();
			friend.setUser1(friendRequest.getUserFrom());
			friend.setUser2(friendRequest.getUserTo());
			friendRepository.save(friend);
			return true;
		}
		return false;
	}

	@Override
	public List<FriendResponse> findSuggestionListByUserId(String userId) {
		return friendRequestRepository.findSuggestionListByUserId(userId);
	}

	@Override
	public List<FriendResponse> findUserToUserIdByUserFromUserIdPageable(String userId) {
		return friendRequestRepository.findUserToUserIdByUserFromUserIdPageable(userId);
	}

	@Override
	public ResponseEntity<GenericResponse> sendFriendRequest(String userId, String userIdToken) {
		Optional<User> user1 = userRepository.findById(userIdToken);
		Optional<User> user2 = userRepository.findById(userId);
		if (user1.isEmpty() || user2.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
		FriendRequest friendRequest = new FriendRequest();
		friendRequest.setActive(true);
		friendRequest.setUserFrom(user1.get());
		friendRequest.setUserTo(user2.get());
		friendRequestRepository.save(friendRequest);

		return ResponseEntity.ok().body(new GenericResponse(true, "Create Successful!",
				new FriendRequestDTO(friendRequest), HttpStatus.OK.value()));

	}

	@Override
	public List<FriendResponse> findUserFromUserIdByUserToUserIdPageable(String userId, Pageable pageable) {
		return friendRequestRepository.findUserFromUserIdByUserToUserIdPageable(userId, pageable);
	}
}
