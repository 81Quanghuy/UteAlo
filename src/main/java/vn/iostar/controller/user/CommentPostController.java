package vn.iostar.controller.user;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CommentUpdateRequest;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.ReplyCommentPostRequestDTO;
import vn.iostar.repository.CommentRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;

@RestController
@RequestMapping(value = "/api/v1/post/comment")
public class CommentPostController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommentService commentService;

	// Lấy danh sách bình luận của 1 bài viết
	@GetMapping("/{postId}")
	public ResponseEntity<GenericResponse> getCommentOfPost(@PathVariable("postId") int postId) {
		return commentService.getCommentOfPost(postId);
	}
	
	// Lấy danh sách phản hổi của 1 bình luận
	@GetMapping("/{commentId}/commentReply")
	public ResponseEntity<GenericResponse> getCommentReplyOfComment(@PathVariable("commentId") int commentId) {
		return commentService.getCommentReplyOfComment(commentId);
	}
	
	@GetMapping("/number/{postId}") 
	public ResponseEntity<GenericResponse> getCountCommentOfPost(
			@PathVariable("postId") int postId) {
			return commentService.getCountCommentOfPost(postId);
	}
	
	// Thêm bình luận
	@PostMapping("/create")
	public ResponseEntity<Object> createCommentPost(@ModelAttribute CreateCommentPostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.createCommentPost(token, requestDTO);
	}
	
	// Phản hồi bình luận
	@PostMapping("/reply")
	public ResponseEntity<Object> replyCommentPost(@ModelAttribute ReplyCommentPostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.replyCommentPost(token, requestDTO);
	}
	
	// Cập nhật bình luận
	@PutMapping("/update/{commentId}")
	public ResponseEntity<Object> updateUser(@ModelAttribute CommentUpdateRequest request,
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") Integer commentId,
			BindingResult bindingResult) throws Exception {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return commentService.updateComment(commentId, request,currentUserId);

	}
	
	// Xóa bình luận
	@PutMapping("/delete/{commentId}")
	public ResponseEntity<GenericResponse> deleteCommentOfPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("commentId") Integer commentId) {
		return commentService.deleteCommentOfPost(commentId);
	}
}
