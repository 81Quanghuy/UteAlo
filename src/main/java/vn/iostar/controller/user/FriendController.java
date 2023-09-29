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
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.FriendRequestService;
import vn.iostar.service.FriendService;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

	@Autowired
	FriendService friendService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	FriendRequestService friendRequestService;
	
	
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

	/**
     * GET list FriendRequest by Authorization
     *
     * @param authorization The JWT (JSON Web Token) provided in the "Authorization" header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
	@GetMapping("/request/list")
	public ResponseEntity<GenericResponse> getRequestList(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(0, 5);
		List<FriendRequestResponse> friend = friendRequestService.findUserFromUserIdByUserToUserIdPageable(userId,pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Retrieved user posts successfully and access update denied").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}	
	
	/**
     * PUT delete FriendRequest by Authorization and userId
     *
     * @param userId
     * @param authorization The JWT (JSON Web Token) provided in the "Authorization" header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
	@PutMapping("/request/delete/{userId}")
	public ResponseEntity<GenericResponse> deleteFriendRequest(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendRequestService.deleteFriendRequest(userId, userIdToken);
	}
	
	/**
     * PUT accept FriendRequest by Authorization and userId
     *
     * @param userId
     * @param authorization The JWT (JSON Web Token) provided in the "Authorization" header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
	@PutMapping("/request/accept/{userId}")
	public ResponseEntity<GenericResponse> updateUser(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId)  {
		String token = authorizationHeader.substring(7);
		String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);

		return friendRequestService.acceptRequest(userId, userIdFromToken);

	}	
	/**
     * GET list FriendRequest by Authorization
     *
     * @param authorization The JWT (JSON Web Token) provided in the "Authorization" header for authentication.
     * @return The resource if found, or a 404 Not Found response.
     */
	@GetMapping("/suggestion/list")
	public ResponseEntity<GenericResponse> getSuggestionList(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(0, 5);
		
		List<FriendRequestResponse> friend = friendRequestService.findSuggestionListByUserId(userId,pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Get Suggestion List Successfully!").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}	
	

}
