package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.PostGroupRequest;
import vn.iostar.entity.User;
import vn.iostar.repository.PostGroupRequestRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.PostGroupRequestService;

@Service
public class PostGroupRequestServiceImpl implements PostGroupRequestService {

	@Autowired
	PostGroupRequestRepository postGroupRequestRepository;

	@Autowired
	UserRepository userRepository;

	@Override
	public <S extends PostGroupRequest> S save(S entity) {
		return postGroupRequestRepository.save(entity);
	}

	@Override
	public List<PostGroupRequest> findAll() {
		return postGroupRequestRepository.findAll();
	}

	@Override
	public Optional<PostGroupRequest> findById(String id) {
		return postGroupRequestRepository.findById(id);
	}

	@Override
	public long count() {
		return postGroupRequestRepository.count();
	}

	@Override
	public List<PostGroupRequest> findByIsAcceptAndPostGroupPostGroupId(Integer postGroupId, Boolean isAccept) {
		return postGroupRequestRepository.findByIsAcceptAndPostGroupPostGroupId(isAccept, postGroupId);
	}

	@Override
	public void delete(PostGroupRequest entity) {
		postGroupRequestRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		postGroupRequestRepository.deleteAll();
	}

	// Hủy những lời mời vào nhóm mà mình đã gửi
	@Override
	public ResponseEntity<GenericResponse> cancelPostGroupInvitation(String postGroupRequestId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}

		Optional<PostGroupRequest> postGroupRequestOptional = postGroupRequestRepository
				.findInvitationSentByUserIdAndRequestId(currentUserId, postGroupRequestId);
		if (postGroupRequestOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
		delete(postGroupRequestOptional.get());
		return ResponseEntity.ok().body(new GenericResponse(true, "Delete Successful!", "None", HttpStatus.OK.value()));

	}

}
