package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.MessageService;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {

	@Autowired
	MessageService messageService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	// Get Message with UserID and AuthorizationHeader
	@GetMapping("/user/{userId}")
	public ResponseEntity<GenericResponse> getListMessageByUserId1AndUserId2(
			@Valid @PathVariable("userId") String userId, @RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return messageService.getListMessageByUserIdAndUserTokenId(userId, userIdToken);
	}

	// Get Message with GroupId and AuthorizationHeader
	@GetMapping("/user/{groupId}")
	public ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserId(
			@Valid @PathVariable("groupId") String groupId,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return messageService.getListMessageByGroupIdAndUserTokenId(groupId, userIdToken);
	}
}
