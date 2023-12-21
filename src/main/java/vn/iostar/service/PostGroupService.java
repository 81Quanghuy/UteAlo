package vn.iostar.service;

import java.util.List;
import java.util.Map;
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

	<S extends PostGroup> S save(S entity);

	Optional<PostGroup> findById(Integer id);

	List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable);

	ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> getSuggestionPostGroupByUserId(String authorizationHeader);

	ResponseEntity<GenericResponse> createPostGroupByUserId(PostGroupDTO postGroup, String authorizationHeader);

	ResponseEntity<GenericResponse> createPostGroupByAdmin(PostGroupDTO postGroup, String authorizationHeader);

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

	ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(PostGroupDTO postGroup, String currentUserId);

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

	ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(PostGroupDTO postGroup, String currentUserId);

	ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(PostGroupDTO postGroup, String currentUserId);

	Optional<PostGroup> findByPostGroupName(String groupName);

	// Đếm số lượng nhóm từng tháng trong năm
	Map<String, Long> countGroupsByMonthInYear();

	// Đếm số lượng user trong ngày hôm nay
	long countGroupsToday();

	// Đếm số lượng user trong 7 ngày
	public long countGroupsInWeek();

	// Đếm số lượng user trong 1 tháng
	long countGroupsInMonthFromNow();

	// Đếm số lượng user trong 1 năm
	long countGroupsInOneYearFromNow();

	// Đếm số lượng user trong 9 tháng
	long countGroupsInNineMonthsFromNow();

	// Đếm số lượng user trong 6 tháng
	long countGroupsInSixMonthsFromNow();

	// Đếm số lượng user trong 3 tháng
	long countGroupsInThreeMonthsFromNow();

	ResponseEntity<GenericResponse> cancelRequestPostGroup(Integer postGroupId, String currentUserId);

	// Lấy danh sách nhóm mà 1 user tham gia có phân trang
	ResponseEntity<GenericResponseAdmin> getPostGroupJoinByUserId(String userId, int page, int itemsPerPage);

	// Thống kê bài post trong ngày hôm nay
	List<SearchPostGroup> getGroupsToday();

	// Thống kê bài post trong 7 ngày
	List<SearchPostGroup> getGroupsIn7Days();

	// Thống kê bài post trong 1 tháng
	List<SearchPostGroup> getGroupsInMonth();

	ResponseEntity<GenericResponse> addAdminRoleInGroup(Integer groupId, String userId, String userIdToken);
}
