package vn.iostar.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import vn.iostar.dto.CountDTO;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
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
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllComments(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int items) {
		return commentService.getAllComments(authorizationHeader, page, items);
	}

	// Admin xóa comment trong hệ thống
	@PutMapping("/delete/{commentId}")
	public ResponseEntity<GenericResponse> deleteCommentOfPost(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") Integer commentId) {
		return commentService.deleteCommentByAdmin(commentId, authorizationHeader);
	}

	// Thêm comment
	@PostMapping("/create")
	public ResponseEntity<Object> createCommentPost(@ModelAttribute CreateCommentPostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return commentService.createCommentPost(token, requestDTO);
	}

	// Đếm số lượng comment từng tháng trong năm
	@GetMapping("/countCommentsByMonthInYear")
	public ResponseEntity<Map<String, Long>> countComemntsByMonthInYear() {
		try {
			Map<String, Long> commentCountsByMonth = commentService.countCommentsByMonthInYear();
			return ResponseEntity.ok(commentCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng group
	@GetMapping("/countComment")
	public ResponseEntity<CountDTO> countCommentsToday() {
		try {
			long commentCountIn1Year = commentService.countCommentsInOneYearFromNow();

			CountDTO groupCountDTO = new CountDTO(0, 0, 0, 0, 0, 0, commentCountIn1Year);
			return ResponseEntity.ok(groupCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
