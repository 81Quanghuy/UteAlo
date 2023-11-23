package vn.iostar.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponseAdmin;
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
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllUsers(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
		return userService.getAllUsers(authorizationHeader, page, items);
	}

	// Cập nhật trạng thái tài khoản của user
	@PutMapping("/update")
	public ResponseEntity<Object> updateUser(@ModelAttribute UserManagerRequest request,
			@RequestHeader("Authorization") String authorizationHeader) {
		return userService.accountManager(authorizationHeader, request);
	}
}
