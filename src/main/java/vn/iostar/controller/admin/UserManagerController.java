package vn.iostar.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.UserManagerRequest;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/admin/userManager")
public class UserManagerController {

	@Autowired
	UserService userService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	// Lấy danh sách tất cả user trong hệ thống
	@GetMapping("/listUsers")
	public ResponseEntity<GenericResponse> getAllUsers(@RequestHeader("Authorization") String authorizationHeader) {
		return userService.getAllUsers(authorizationHeader);
	}

	// Cập nhật trạng thái tài khoản của user
	@PutMapping("/updateInfo")
	public ResponseEntity<Object> updateUser(@ModelAttribute UserManagerRequest request,
			@RequestHeader("Authorization") String authorizationHeader) {
		return userService.accountManager(authorizationHeader, request);
	}
}
