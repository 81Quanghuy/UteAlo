package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.dto.SearchPostGroup;
import vn.iostar.entity.PostGroup;

public interface PostGroupService {

	Optional<PostGroup> findById(Integer id);

	List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable);

	ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> getSuggestionPostGroupByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> createPostGroupByUserId(PostGroupDTO postGroup, String authorizationHeader);

	ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> deletePostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> acceptPostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> declinePostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> acceptMemberPostGroup(PostGroupDTO postGroup, String currentUserId);

	// Lời mời vào nhóm đã nhận được
	ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(String currentUserId);

	// Lời mời vào nhóm đã gửi đi
	ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(String currentUserId);

	// Rời nhóm
	ResponseEntity<GenericResponse> leaveGroup(String userId, Integer groupId);

	// Tìm kiếm nhóm
	ResponseEntity<GenericResponse> findByPostGroupNameContainingIgnoreCase(String search, String userIdToken);

	// Tìm kiếm tất cả nhóm và người dùng
	ResponseEntity<GenericResponse> searchGroupAndUserContainingIgnoreCase(String search, String userIdToken);

	ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, Integer postId);

	ResponseEntity<GenericResponse> joinPostGroup(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> getMemberByPostId(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> getMemberRequiredByPostId(Integer postId, String currentUserId);

	ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> deleteMemberByPostId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> declineMemberRequiredByPostId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader);

	int getNumberOfFriendsInGroup(String userId, int postGroupId);

	// Tìm tất cả user trong hệ thống
	Page<SearchPostGroup> findAllGroups(int page, int itemsPerPage);

	// Lấy danh sách tất cả user trong hệ thống
	ResponseEntity<GenericResponseAdmin> getAllGroups(String authorizationHeader, int page, int itemsPerPage);
	
	// Admin xóa nhóm trong hệ thống
	ResponseEntity<GenericResponse> deletePostGroupByAdmin(Integer postId, String authorizationHeader);
}
