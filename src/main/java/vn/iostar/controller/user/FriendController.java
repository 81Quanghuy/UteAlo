package vn.iostar.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.FriendService;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

	@Autowired
	FriendService friendService;
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@GetMapping("/list/{userId}")
	public ResponseEntity<GenericResponse> getUserPosts(@Valid @PathVariable("userId") String userId) {
		Pageable pageable = PageRequest.of(0, 5);
		List<FriendRequestResponse> friend = friendService.findFriendUserIdsByUserId(userId,pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Retrieved user posts successfully and access update denied").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}

	@PutMapping("/delete")
	public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendService.deleteFriend(userId, userIdToken);
	}

}
