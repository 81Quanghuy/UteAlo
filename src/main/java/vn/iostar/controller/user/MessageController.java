package vn.iostar.controller.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.MessageRequest;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.MessageService;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

	@Autowired
	MessageService messageService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	// Get Message with UserID and AuthorizationHeader
	@GetMapping("/get/user/{userId}")
	public ResponseEntity<GenericResponse> getListMessageByUserId1AndUserId2(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size, @PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		PageRequest pageable = PageRequest.of(page, size);
		return messageService.getListMessageByUserIdAndUserTokenId(userId, userIdToken, pageable);
	}

	// Get Message with GroupId and AuthorizationHeader
	@GetMapping("/get/group/{groupId}")
	public ResponseEntity<GenericResponse> getListMessageByGroupIdAndUserId(
			@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer size,
			@PathVariable("groupId") String groupId) {
		PageRequest pageable = PageRequest.of(page, size);
		return messageService.getListMessageByGroupIdAndUserTokenId(groupId, pageable);
	}

	// delete one message by user
	@PutMapping("/delete")
	public ResponseEntity<GenericResponse> deleteMessages(@RequestBody MessageRequest messageRequest,
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return messageService.deleteMessage(userIdToken, messageRequest);
	}

	@GetMapping("/list/react/{createAt}")
	public ResponseEntity<GenericResponse> getListReactInMessage(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size, @PathVariable("createAt") Date createAt) {
		PageRequest pageable = PageRequest.of(page, size);
		return messageService.getListReactInMessage(createAt, pageable);
	}

}
