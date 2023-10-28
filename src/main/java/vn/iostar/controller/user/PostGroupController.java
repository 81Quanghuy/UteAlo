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

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostGroupService;

@RestController
@RequestMapping("/api/v1/groupPost")
public class PostGroupController {

	@Autowired
	PostGroupService groupService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

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

	@GetMapping("/list/invited")
	public ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupInvitedByUserId(currentUserId);
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

	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deletePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.deletePostGroup(postId, currentUserId);
	}

	@PostMapping("/accept/{postId}")
	public ResponseEntity<GenericResponse> acceptPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptPostGroup(postId, currentUserId);
	}

	@PostMapping("/decline/{postId}")
	public ResponseEntity<GenericResponse> declinePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.declinePostGroup(postId, currentUserId);
	}

	@PostMapping("/invite")
	public ResponseEntity<GenericResponse> invitePostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.invitePostGroup(postGroup, currentUserId);
	}

	@PostMapping("/joinGroup/{postId}")
	public ResponseEntity<GenericResponse> joinPostGroup(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.joinPostGroup(postId, currentUserId);
	}

	@PostMapping("/acceptMember")
	public ResponseEntity<GenericResponse> acceptMemberPostGroup(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody PostGroupDTO postGroup) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.acceptMemberPostGroup(postGroup, currentUserId);
	}

	@GetMapping("/get/{postId}")
	public ResponseEntity<GenericResponse> getPostGroupById(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getPostGroupById(currentUserId, postId);
	}

	@GetMapping("/list/member/{postId}")
	public ResponseEntity<GenericResponse> getMemberByPostId(@PathVariable("postId") Integer postId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberByPostId(postId, currentUserId);
	}

	@GetMapping("/list/memberRequired/{postId}")
	public ResponseEntity<GenericResponse> getMemberRequiredByPostId(@PathVariable("postId") Integer postId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return groupService.getMemberRequiredByPostId(postId, currentUserId);
	}
}
