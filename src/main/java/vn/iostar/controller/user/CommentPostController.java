package vn.iostar.controller.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.repository.CommentRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;

@RestController
@RequestMapping("/api/v1/post/comment")
public class CommentPostController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommentService commentService;

	@GetMapping("/{postId}")
	public ResponseEntity<GenericResponse> getLikeOfPost(@PathVariable("postId") int postId) {
		return commentService.getCommentOfPost(postId);
	}
	
	@GetMapping("/number/{postId}") 
	public ResponseEntity<GenericResponse> getNumberLikeOfPost(
			@PathVariable("postId") int postId) {
			return commentService.getCountCommentOfPost(postId);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Object> createCommentPost(@RequestBody CreateCommentPostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.createCommentPost(token, requestDTO);
	}
	
	@PutMapping("/delete/{commentId}")
	public ResponseEntity<GenericResponse> deleteCommentOfPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("commentId") Integer commentId) {
		return commentService.deleteCommentOfPost(commentId);

	}
}
