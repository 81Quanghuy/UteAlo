
package vn.iostar.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.FilesOfGroupDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.PostGroup;
import vn.iostar.dto.PhotosOfGroupDTO;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostGroupRequestService;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.PostService;
import vn.iostar.service.ShareService;

@RestController
@RequestMapping("/api/v1/groupPost")
public class PostGroupController {

	@Autowired
	PostGroupService groupService;

	@Autowired
	PostGroupRequestService postGroupRequestService;

	@Autowired
	PostGroupRepository postGroupRepository;

	@Autowired
	ShareService shareService;

	@Autowired
	PostService postService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	// Get list all group
	@GetMapping("/list/all")
	public ResponseEntity<GenericResponse> getPostGroupByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupByUserId(authorizationHeader);
	}

	// get list group user join
	@GetMapping("/list/join")
	public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupJoinByUserId(authorizationHeader);
	}

	// get list group user owner
	@GetMapping("/list/owner")
	public ResponseEntity<GenericResponse> getPostGroupOwenrByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupOwnerByUserId(authorizationHeader);
	}

	// Lời mời vào nhóm đã nhận được
	@GetMapping("/list/isInvited")
	public ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupInvitedByUserId(currentUserId);
	}

	// Lời mời vào nhóm đã gửi đi
	@GetMapping("/list/invited")
	public ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupRequestsSentByUserId(currentUserId);
	}

	// Chưa xong
	@GetMapping("/list/suggestion")
	public ResponseEntity<GenericResponse> getSuggestionPostGroupByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getSuggestionPostGroupByUserId(authorizationHeader);
	}

	// create group
	@PostMapping("/create")
	public ResponseEntity<GenericResponse> createGroupByUser(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.createPostGroupByUserId(postGroup, authorizationHeader);
	}

	// update infor post group
	@PutMapping("/update/bio")
	public ResponseEntity<GenericResponse> updatePostGroupByPostId(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.updatePostGroupByPostIdAndUserId(postGroup, currentUserId);
	}

	// update avatar and background in group
	@PutMapping("/update/photo")
	public ResponseEntity<GenericResponse> updateBackgroundByPostId(
			@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.updatePhotoByPostIdAndUserId(postGroup, currentUserId);
	}

	// delete group
	@DeleteMapping("/delete/{postGroupId}")
	public ResponseEntity<GenericResponse> deletePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.deletePostGroup(postGroupId, currentUserId);
	}

	// accept request in group
	@PostMapping("/accept/{postGroupId}")
	public ResponseEntity<GenericResponse> acceptPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptPostGroup(postGroupId, currentUserId);
	}

	// Hủy yêu cầu tham gia nhóm
	@PutMapping("/cancel/request/group/{postGroupId}")
	public ResponseEntity<GenericResponse> cancelRequestPostGroup(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.cancelRequestPostGroup(postGroupId, currentUserId);
	}

	// decline request in group
	@PostMapping("/decline/{postGroupId}")
	public ResponseEntity<GenericResponse> declinePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.declinePostGroup(postGroupId, currentUserId);
	}

	// invite member group
	@PostMapping("/invite")
	public ResponseEntity<GenericResponse> invitePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.invitePostGroup(postGroup, currentUserId);
	}

	// join group by userId
	@PostMapping("/joinGroup/{postGroupId}")
	public ResponseEntity<GenericResponse> joinPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.joinPostGroup(postGroupId, currentUserId);
	}

	// accept one request member in all request member
	@PostMapping("/acceptMember")
	public ResponseEntity<GenericResponse> acceptMemberPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptMemberPostGroup(postGroup, currentUserId);
	}

	// get information post group
	@GetMapping("/get/{postGroupId}")
	public ResponseEntity<GenericResponse> getPostGroupById(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupById(currentUserId, postGroupId);
	}

	// Lấy những bài share post của nhóm
	@GetMapping("/{postGroupId}/shares")
	public ResponseEntity<GenericResponse> getGroupSharePosts(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return shareService.getGroupSharePosts(currentUserId, postGroupId, page, size);
	}

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@GetMapping("/posts")
	public ResponseEntity<GenericResponse> getPostOfUserPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(page, size);
		return postService.getPostOfPostGroup(currentUserId, pageable);

	}

	// list member in group by postGroupId
	@GetMapping("/list/member/{postGroupId}")
	public ResponseEntity<GenericResponse> getMemberByPostId(@PathVariable("postGroupId") Integer postGroupId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberByPostId(postGroupId, currentUserId);
	}

	// list request member in group
	@GetMapping("/list/memberRequired/{postGroupId}")
	public ResponseEntity<GenericResponse> getMemberRequiredByPostId(@PathVariable("postGroupId") Integer postGroupId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberRequiredByPostId(postGroupId, currentUserId);
	}

	// them quyen pho quan trị viên
	@PostMapping("/appoint-deputy")
	public ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.assignDeputyByUserIdAndGroupId(postGroup, currentUserId);
	}

	// nhuong quyen âdmin
	@PostMapping("/appoint-admin")
	public ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.assignAdminByUserIdAndGroupId(postGroup, currentUserId);
	}

	// xóa quyền phó quản trị viên
	@PostMapping("/remove-deputy")
	public ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.removeDeputyByUserIdAndGroupId(postGroup, currentUserId);
	}

	// delete member
	@PostMapping("/delete/member")
	public ResponseEntity<GenericResponse> deteleMemberByPostId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.deleteMemberByPostId(postGroup, currentUserId);
	}

	// decline request member in group
	@PostMapping("/decline/memberRequired")
	public ResponseEntity<GenericResponse> declineMemberRequiredByPostId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.declineMemberRequiredByPostId(postGroup, currentUserId);
	}

	// Lấy tất cả bài post của 1 nhóm
	@GetMapping("/{postGroupId}/posts")
	public ResponseEntity<GenericResponse> getPostOfPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable Integer postGroupId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return postService.getGroupPosts(userIdToken, postGroupId, page, size);
	}

	// Hủy những lời mời vào nhóm mà mình đã gửi
	@PutMapping("/request/cancel/{postGroupRequestId}")
	public ResponseEntity<GenericResponse> cancelRequestJoinInGroup(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupRequestId") String postGroupRequestId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return postGroupRequestService.cancelPostGroupInvitation(postGroupRequestId, userIdToken);
	}

	@PutMapping("/leaveGroup/{postGroupId}")
	public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.leaveGroup(userIdToken, postGroupId);
	}

	@GetMapping("/getPostGroups/key")
	public ResponseEntity<GenericResponse> searchPostGroups(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam("search") String search) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.findByPostGroupNameContainingIgnoreCase(search, userIdToken);
	}

	// Lấy danh sách file của 1 nhóm
	@GetMapping("/files/{groupId}")
	public List<FilesOfGroupDTO> getLatestFilesOfGroup(@PathVariable("groupId") Integer groupId) {
		return postService.findLatestFilesByGroupId(groupId);
	}

	// Lấy danh sách photo của 1 nhóm
	@GetMapping("/photos/{groupId}")
	public Page<PhotosOfGroupDTO> getLatestPhotoOfGroup(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @PathVariable("groupId") Integer groupId) {
		return postService.findLatestPhotosByGroupId(groupId, page, size);
	}

	// Lấy những bài viết trong nhóm do Admin đăng
	@GetMapping("/roleAdmin/{groupId}")
	public ResponseEntity<GenericResponse> getPostsByAdminRoleInGroup(@PathVariable("groupId") Integer groupId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		List<PostsResponse> groupPosts = postService.findPostsByAdminRoleInGroup(groupId, pageable);
		Optional<PostGroup> postGroup = groupService.findById(groupId);
		if (postGroup.isEmpty()) {
			throw new RuntimeException("Group not found.");
		} else if (groupPosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(GenericResponse.builder().success(false).message("No posts found for admin of this group")
							.statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Retrieved posts of admin successfully")
							.result(groupPosts).statusCode(HttpStatus.OK.value()).build());
		}
	}

	// thêm quyền admin cho thành viên trong nhóm
	// Các trường hợp : 1 Nó chưa tham gia nhóm đó và nhóm đó chưa có admin : Thêm
	// thẳng thành viên đó vào nhóm là admin
	// 2 : Nó chưa tham gia và đã có admin: Chuyển quyền admin cho user này
	// 3 Nó đã tham gia và chưa có admin: Thay đổi quyền
	// 4 Nó đã tham gia và đã có admin : Chuyển quyền
	
	@PostMapping("/addAdmin")
	public ResponseEntity<GenericResponse> addAdminRoleInGroup(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam int groupId,
			@RequestParam String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.addAdminRoleInGroup(groupId, userId, userIdToken);
	}

}
