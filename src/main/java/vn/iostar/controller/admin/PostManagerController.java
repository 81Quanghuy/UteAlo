package vn.iostar.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
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
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllPosts(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
		return postService.getAllPosts(authorizationHeader, page, items);
	}

	// Xóa bài post trong hệ thống
	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		return postService.deletePostByAdmin(postId, authorizationHeader);
	}
	
	// Thêm bài post
	@PostMapping("/create")
	public ResponseEntity<Object> createPost(@ModelAttribute CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return postService.createUserPost(token, requestDTO);
	}

}
