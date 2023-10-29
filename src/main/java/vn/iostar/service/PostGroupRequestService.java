package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.PostGroupRequest;

public interface PostGroupRequestService {

	void deleteAll();

	void delete(PostGroupRequest entity);

	long count();

	Optional<PostGroupRequest> findById(String id);

	List<PostGroupRequest> findAll();

	<S extends PostGroupRequest> S save(S entity);

	List<PostGroupRequest> findByIsAcceptAndPostGroupPostGroupId(Integer postGroupId, Boolean isAccept);

	// Hủy những lời mời vào nhóm mà mình đã gửi
	ResponseEntity<GenericResponse> cancelPostGroupInvitation(String postGroupRequestId, String currentUserId);

}
