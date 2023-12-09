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
@RequestMapping("/api/v1/comment/like")
public class LikeCommentController {
	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	LikeService likeService;

	@Autowired
	LikeRepository likeRepository;

	@GetMapping("/{commentId}")
	public ResponseEntity<GenericResponse> getLikeOfComment(@PathVariable("commentId") int commentId) {
		return likeService.getLikeOfComment(commentId);
	}

	@GetMapping("/number/{commentId}")
	public ResponseEntity<GenericResponse> getNumberLikeOfComment(@PathVariable("commentId") int commentId) {
		return likeService.getCountLikeOfComment(commentId);
	}

	@PostMapping("/toggleLike/{commentId}")
	public ResponseEntity<Object> toggleLikeComment(@PathVariable("commentId") int commentId,
			@RequestHeader("Authorization") String token) {
		return likeService.toggleLikeComment(token, commentId);
	}

	@GetMapping("/checkUser/{commentId}")
	public ResponseEntity<Object> checkUserLikePost(@PathVariable("commentId") int commentId,
			@RequestHeader("Authorization") String token) {
		return likeService.checkUserLikeComment(token, commentId);
	}

	// Lấy danh sách những người đã like comment
	@GetMapping("/listUser/{commentId}")
	public ResponseEntity<Object> listUserLikeComment(@PathVariable("commentId") int commentId) {
		return likeService.listUserLikeComment(commentId);
	}
}
