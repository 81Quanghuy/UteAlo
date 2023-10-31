package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostGroupDTO;
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
	ShareService shareService;

	@Autowired
	PostService postService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@GetMapping("/list/all")
	public ResponseEntity<GenericResponse> getPostGroupByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupByUserId(authorizationHeader);
	}
	
	@GetMapping("/list/join")
	public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupJoinByUserId(authorizationHeader);
	}

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

	@PostMapping("/create")
	public ResponseEntity<GenericResponse> createGroupByUser(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.createPostGroupByUserId(postGroup, authorizationHeader);
	}

	@PutMapping("/update/bio")
	public ResponseEntity<GenericResponse> updatePostGroupByPostId(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.updatePostGroupByPostIdAndUserId(postGroup, currentUserId);
	}

	@PutMapping("/update/photo")
	public ResponseEntity<GenericResponse> updateBackgroundByPostId(
			@RequestHeader("Authorization") String authorizationHeader, @ModelAttribute PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.updatePhotoByPostIdAndUserId(postGroup, currentUserId);
	}

	@PutMapping("/delete/{postGroupId}")
	public ResponseEntity<GenericResponse> deletePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.deletePostGroup(postGroupId, currentUserId);
	}

	@PostMapping("/accept/{postGroupId}")
	public ResponseEntity<GenericResponse> acceptPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptPostGroup(postGroupId, currentUserId);
	}

	@PostMapping("/decline/{postGroupId}")
	public ResponseEntity<GenericResponse> declinePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.declinePostGroup(postGroupId, currentUserId);
	}

	@PostMapping("/invite")
	public ResponseEntity<GenericResponse> invitePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.invitePostGroup(postGroup, currentUserId);
	}

	@PostMapping("/joinGroup/{postGroupId}")
	public ResponseEntity<GenericResponse> joinPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.joinPostGroup(postGroupId, currentUserId);
	}

	@PostMapping("/acceptMember")
	public ResponseEntity<GenericResponse> acceptMemberPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptMemberPostGroup(postGroup, currentUserId);
	}

	@GetMapping("/get/{postGroupId}")
	public ResponseEntity<GenericResponse> getPostGroupById(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupById(currentUserId, postGroupId);
	}

	// Lấy những bài share post của nhóm
	@GetMapping("/{postGroupId}/shares")
	public ResponseEntity<GenericResponse> getGroupSharePosts(@PathVariable("postGroupId") Integer postGroupId) {
		return shareService.getGroupSharePosts(postGroupId);
	}

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@GetMapping("/posts/{userId}")
	public ResponseEntity<GenericResponse> getPostOfUserPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable String userId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return postService.getPostOfPostGroup(currentUserId, userId);

	}
	@GetMapping("/list/member/{postGroupId}")
	public ResponseEntity<GenericResponse> getMemberByPostId(@PathVariable("postGroupId") Integer postGroupId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberByPostId(postGroupId, currentUserId);
	}

	@GetMapping("/list/memberRequired/{postGroupId}")
	public ResponseEntity<GenericResponse> getMemberRequiredByPostId(@PathVariable("postGroupId") Integer postGroupId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberRequiredByPostId(postGroupId, currentUserId);
	}

	@PostMapping("/appoint-admin")
	public ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.assignAdminByUserIdAndGroupId(postGroup, currentUserId);
	}

	@PostMapping("/delete/member")
	public ResponseEntity<GenericResponse> deteleMemberByPostId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.deleteMemberByPostId(postGroup, currentUserId);
	}

	@PostMapping("/decline/memberRequired")
	public ResponseEntity<GenericResponse> declineMemberRequiredByPostId(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.declineMemberRequiredByPostId(postGroup, currentUserId);

	// Lấy tất cả bài post của 1 nhóm
	@GetMapping("/{postGroupId}/posts")
	public ResponseEntity<GenericResponse> getPostOfPostGroup(@PathVariable Integer postGroupId) {
		return postService.getGroupPosts(postGroupId);
	}

	@PutMapping("/request/cancel/{postGroupRequestId}")
	public ResponseEntity<GenericResponse> cancelRequestJoinInGroup(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupRequestId") String postGroupRequestId) {
		System.out.print("authorizationHeader" + authorizationHeader);
		System.out.print("postGroupRequestId" + postGroupRequestId);
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

}
