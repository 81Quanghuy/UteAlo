package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreateCommentShareRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.ReplyCommentShareRequestDTO;
import vn.iostar.repository.CommentRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;

@RestController
@RequestMapping(value = "/api/v1/share/comment")
public class CommentShareController {
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	CommentService commentService;

	@GetMapping("/{shareId}")
	public ResponseEntity<GenericResponse> getCommentOfShare(@PathVariable("shareId") int shareId) {
		return commentService.getCommentOfShare(shareId);
	}
	
	@GetMapping("/{commentId}/commentReply")
	public ResponseEntity<GenericResponse> getCommentReplyOfComment(@PathVariable("commentId") int commentId) {
		return commentService.getCommentReplyOfCommentShare(commentId);
	}
	
	@PostMapping("/create")
	public ResponseEntity<Object> createCommentShare(@ModelAttribute CreateCommentShareRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.createCommentShare(token, requestDTO);
	}
	
	@PostMapping("/reply")
	public ResponseEntity<Object> replyCommentPost(@ModelAttribute ReplyCommentShareRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.replyCommentShare(token, requestDTO);
	}
}
