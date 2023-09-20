package vn.iostar.controller.user;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreateLikePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Like;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.LikeService;

@RestController
@RequestMapping("/api/v1/like")
public class LikePostController {
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	LikeService likeService;
	
	@Autowired
	LikeRepository likeRepository;
	
	@GetMapping("/post/{postId}") 
	public ResponseEntity<GenericResponse> getLikeOfPost(
			@PathVariable("postId") int postId) {
		List<Like> like = likeRepository.findByPostPostId(postId);
		if(like.isEmpty()) {
			throw new RuntimeException("This post has no like.");
		} else {
			return likeService.getLikeOfPost(postId);
		}
	}
	
	@GetMapping("/post/number/{postId}") 
	public ResponseEntity<GenericResponse> getNumberLikeOfPost(
			@PathVariable("postId") int postId) {
		List<Like> like = likeRepository.findByPostPostId(postId);
		if(like.isEmpty()) {
			throw new RuntimeException("This post has no like.");
		} else {
			return likeService.getCountLikeOfPost(postId);
		}
	}
	
	@PostMapping("/toggleLike")
	public ResponseEntity<Object> createPost(@RequestBody CreateLikePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return likeService.toggleLike(token, requestDTO);
	}
}
