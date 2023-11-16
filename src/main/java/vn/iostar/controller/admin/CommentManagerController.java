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
import vn.iostar.service.CommentService;

@RestController
@RequestMapping("/api/v1/admin/commentManager")
public class CommentManagerController {
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	
	// Lấy tất cả comment trong hệ thống
	@GetMapping("/listComments")
	public ResponseEntity<GenericResponse> getAllComments(@RequestHeader("Authorization") String authorizationHeader) {
		return commentService.getAllComments(authorizationHeader);
	}
	
	// Admin xóa comment trong hệ thống
	@PutMapping("/delete/{commentId}")
	public ResponseEntity<GenericResponse> deleteCommentOfPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("commentId") Integer commentId) {
		return commentService.deleteCommentByAdmin(commentId,authorizationHeader);
	}
}
