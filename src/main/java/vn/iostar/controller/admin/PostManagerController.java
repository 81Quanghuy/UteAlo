package vn.iostar.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostService;

@RestController
@RequestMapping("/api/v1/admin/postManager")
public class PostManagerController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;
	
	// Lấy tất cả bài post trong hệ thống
	@GetMapping("/listPosts")
	public ResponseEntity<GenericResponse> getAllPosts(@RequestHeader("Authorization") String authorizationHeader) {
		return postService.getAllPosts(authorizationHeader);
	}
	
	// Xóa bài post trong hệ thống
	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String token,
			@PathVariable("postId") Integer postId) {
		return postService.deletePostByAdmin(postId, token);

	}

}
