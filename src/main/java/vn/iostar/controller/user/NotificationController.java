package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.NotificationDTO;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.NotificationService;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private JwtTokenProvider jwtTokenProvider;

	// Lấy danh sách thông báo của user theo authenticaion header
	@GetMapping("/get")
	public ResponseEntity<GenericResponse> getListNotificationByUserId(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(page, size);
		return notificationService.getListNotificationByUserId(userIdToken, pageable);
	}

	// Đánh dấu đã đọc thông báo
	@PutMapping("/read/{notificationId}")
	public ResponseEntity<GenericResponse> readNotification(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable String notificationId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return notificationService.readNotification(userIdToken, notificationId);
	}

	// Xóa thông báo
	@DeleteMapping("/delete/{notificationId}")
	public ResponseEntity<GenericResponse> deleteNotification(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable String notificationId) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return notificationService.deleteNotification(userIdToken, notificationId);
	}

	// Xóa tất cả thông báo cuar user
	@DeleteMapping("/deleteAll")
	public ResponseEntity<GenericResponse> deleteAllNotification(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return notificationService.deleteAllNotification(userIdToken);
	}

	// Tao thong bao cho user qua NotificationDTO
	@PostMapping("/create")
	public ResponseEntity<GenericResponse> createNotification(
			@RequestHeader("Authorization") String authorizationHeader, @RequestBody NotificationDTO notificationDTO) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return notificationService.createNotification(userIdToken, notificationDTO);
	}

	// Đánh đấu thông báo đã đọc
	@PutMapping("/unread-all")
	public ResponseEntity<GenericResponse> unReadNotification(
			@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userIdToken = jwtTokenProvider.getUserIdFromJwt(token);
		return notificationService.unReadNotification(userIdToken);
	}
}
