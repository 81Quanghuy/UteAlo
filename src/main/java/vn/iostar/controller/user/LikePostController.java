package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import vn.iostar.dto.GenericResponse;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.LikeService;

@RestController
@RequestMapping("/api/v1/post/like")
public class LikePostController {
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	LikeService likeService;
	
	@Autowired
	LikeRepository likeRepository;
	
	@GetMapping("/{postId}") 
	public ResponseEntity<GenericResponse> getLikeOfPost(
			@PathVariable("postId") int postId) {
			return likeService.getLikeOfPost(postId);
	}
	
	@GetMapping("/number/{postId}") 
	public ResponseEntity<GenericResponse> getNumberLikeOfPost(
			@PathVariable("postId") int postId) {
			return likeService.getCountLikeOfPost(postId);
	}
	
	@PostMapping("/toggleLike/{postId}")
	public ResponseEntity<Object> toggleLikePost(@PathVariable("postId") int postId,
			@RequestHeader("Authorization") String token) {
		return likeService.toggleLikePost(token, postId);
	}
	
	@GetMapping("/checkUser/{postId}")
	public ResponseEntity<Object> checkUserLikePost(@PathVariable("postId") int postId,
			@RequestHeader("Authorization") String token) {
		return likeService.checkUserLikePost(token, postId);
	}
	
	// Lấy danh sách những người đã like bài post
	@GetMapping("/listUser/{postId}")
	public ResponseEntity<Object> listUserLikePost(@PathVariable("postId") int postId) {
		return likeService.listUserLikePost(postId);
	}
	
}
