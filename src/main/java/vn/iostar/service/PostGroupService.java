package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.entity.PostGroup;

public interface PostGroupService {

	Optional<PostGroup> findById(Integer id);

	List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable);
	
	ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String authorizationHeader);
	
	ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> getSuggestionPostGroupByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> createPostGroupByUserId(PostGroupDTO postGroup,String authorizationHeader);

	ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> deletePostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> acceptPostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> declinePostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> acceptMemberPostGroup(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(String currentUserId);

	ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, Integer postId);

	ResponseEntity<GenericResponse> joinPostGroup(Integer postId, String currentUserId);
}
