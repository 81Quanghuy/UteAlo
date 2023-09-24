package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
	public List<String> findUserFromUserIdByUserToUserId(String userId) {
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

	public List<FriendRequest> findByUserFromUserIdAndUserToUserId(String userFromId, String userToID) {
		return friendRequestRepository.findByUserFromUserIdAndUserToUserId(userFromId, userToID);
	}

	@Override
	public ResponseEntity<GenericResponse> deleteFriendRequest(String userFromId, String userToId) {
		Optional<User> user = userRepository.findById(userFromId);
		if (!user.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}

		List<FriendRequest> reList = findByUserFromUserIdAndUserToUserId(userFromId, userToId);
		if (!reList.isEmpty()) {
			deleteById(reList.get(0).getFriendRequestId());
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
		List<FriendRequest> reList = findByUserFromUserIdAndUserToUserId(userFromId, userToId);
		if (!reList.isEmpty()) {
			if(Boolean.TRUE.equals(createFriend(reList.get(0)))) {
				deleteById(reList.get(0).getFriendRequestId());

				return ResponseEntity.ok()
						.body(new GenericResponse(true, "Accept Successful!", null, HttpStatus.OK.value()));
			}
			else {
				return ResponseEntity.ok()
						.body(new GenericResponse(false, "Both was friend before ", null, HttpStatus.CREATED.value()));
			}
			
		}
		return ResponseEntity.ok()
				.body(new GenericResponse(false, "NOT FOUND FRIEND REQUEST!", null, HttpStatus.NOT_FOUND.value()));
	}

	private Boolean createFriend(FriendRequest friendRequest) {
		Friend friend2 = friendRepository.findByUser1UserIdAndUser2UserId(friendRequest.getUserFrom().getUserId(),
				friendRequest.getUserTo().getUserId());
		Friend friend3 = friendRepository.findByUser1UserIdAndUser2UserId(friendRequest.getUserTo().getUserId(),
				friendRequest.getUserFrom().getUserId());
		if (friend2 == null && friend3 == null) {
			Friend friend = new Friend();
			friend.setUser1(friendRequest.getUserFrom());
			friend.setUser2(friendRequest.getUserTo());
			friendRepository.save(friend);
			return true;
		}
		return false;
	}

}
