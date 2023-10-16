package vn.iostar.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {

	@Autowired
	FriendService friendService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	FriendRequestService friendRequestService;

	@Autowired
	UserService userService;

	@GetMapping("/list/{userId}")
	public ResponseEntity<GenericResponse> getListFriendByUserId(@Valid @PathVariable("userId") String userId) {
		List<FriendRequestResponse> friend = friendService.findFriendUserIdsByUserId(userId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully")
				.result(friend).statusCode(HttpStatus.OK.value()).build());
	}

	@GetMapping("/status/{userId}")
	public ResponseEntity<GenericResponse> getStatusByUserId(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendService.getStatusByUserId(userId,userIdToken);
	}
	@GetMapping("/list/pageable/{userId}")
	public ResponseEntity<GenericResponse> getListFriendTop10ByUserId(@Valid @PathVariable("userId") String userId) {
		PageRequest pageable = PageRequest.of(0, 10);
		List<FriendRequestResponse> friend = friendService.findFriendTop10UserIdsByUserId(userId, pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get List Friend Successfully")
				.result(friend).statusCode(HttpStatus.OK.value()).build());
	}

	@PutMapping("/delete/{userId}")
	public ResponseEntity<GenericResponse> deleteFriend(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendService.deleteFriend(userId, userIdToken);
	}

	/**
	 * GET list FriendRequest by Authorization
	 *
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@GetMapping("/request/list")
	public ResponseEntity<GenericResponse> getRequestList(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		List<FriendRequestResponse> friend = friendRequestService.findUserFromUserIdByUserToUserId(userId);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Retrieved user posts successfully and access update denied").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}

	/**
	 * GET list FriendRequest by Authorization
	 *
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@GetMapping("/request/list/pageable")
	public ResponseEntity<GenericResponse> getRequestListTop5(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		PageRequest pageable = PageRequest.of(0, 10);
		List<FriendRequestResponse> friend = friendRequestService.findUserFromUserIdByUserToUserIdPageable(userId,
				pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Retrieved user posts successfully and access update denied").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}

	/**
	 * GET list FriendRequest by Authorization
	 *
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@PostMapping("/request/send/{userId}")
	public ResponseEntity<GenericResponse> sendFriendRequest(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendRequestService.sendFriendRequest(userId, userIdToken);

	}

	/**
	 * GET list FriendRequest by Authorization
	 *
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@GetMapping("/requestFrom/list")
	public ResponseEntity<GenericResponse> getRequestListFrom(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		List<FriendRequestResponse> friend = friendRequestService.findUserToUserIdByUserFromUserIdPageable(userId);
		return ResponseEntity.ok(GenericResponse.builder().success(true)
				.message("Retrieved user posts successfully and access update denied").result(friend)
				.statusCode(HttpStatus.OK.value()).build());
	}

	/**
	 * PUT delete FriendRequest by Authorization and userId
	 *
	 * @param userId
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@PutMapping("/request/delete/{userId}")
	public ResponseEntity<GenericResponse> deleteFriendRequest(
			@RequestHeader("Authorization") String authorizationHeader, @Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendRequestService.deleteFriendRequest(userId, userIdToken);
	}

	/**
	 * PUT delete FriendRequest by Authorization and userId
	 *
	 * @param userId
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@PutMapping("/request/cancel/{userId}")
	public ResponseEntity<GenericResponse> cancelRequestFriend(
			@RequestHeader("Authorization") String authorizationHeader, @Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return friendRequestService.deleteFriendRequest(userIdToken, userId);
	}

	/**
	 * PUT accept FriendRequest by Authorization and userId
	 *
	 * @param userId
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@PutMapping("/request/accept/{userId}")
	public ResponseEntity<GenericResponse> updateUser(@RequestHeader("Authorization") String authorizationHeader,
			@Valid @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);

		return friendRequestService.acceptRequest(userId, userIdFromToken);

	}

	/**
	 * GET list FriendRequest by Authorization
	 *
	 * @param authorization The JWT (JSON Web Token) provided in the "Authorization"
	 *                      header for authentication.
	 * @return The resource if found, or a 404 Not Found response.
	 */
	@GetMapping("/suggestion/list")
	public ResponseEntity<GenericResponse> getSuggestionList(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		List<FriendRequestResponse> friend = friendRequestService.findSuggestionListByUserId(userId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get Suggestion List Successfully!")
				.result(friend).statusCode(HttpStatus.OK.value()).build());
	}

}
