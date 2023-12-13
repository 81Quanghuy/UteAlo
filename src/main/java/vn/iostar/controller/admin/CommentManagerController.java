package vn.iostar.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.format.annotation.DateTimeFormat;
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

import vn.iostar.dto.CommentsResponse;
import vn.iostar.dto.CountDTO;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PaginationInfo;
import vn.iostar.entity.User;
import vn.iostar.repository.CommentRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/admin/commentManager")
public class CommentManagerController {

	@Autowired
	CommentService commentService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	CommentRepository commentRepository;

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

	// Thống kê bài post trong ngày hôm nay
	// Thống kê bài post trong 1 ngày
	// Thống kê bài post trong 7 ngày
	// Thống kê bài post trong 1 tháng
	@GetMapping("/filterByDate")
	public List<CommentsResponse> getPosts(@RequestParam(required = false) String action,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		switch (action != null ? action.toLowerCase() : "") {
		case "today":
			return commentService.getCommentsToday();
		case "7days":
			return commentService.getCommentsIn7Days();
		case "month":
			return commentService.getCommentsIn1Month();
		default:
			// Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
			// hoặc một giá trị mặc định
			break;
		}
		// Trả về null hoặc danh sách rỗng tùy theo logic của bạn
		return null;
	}
	
	// Cập nhật controller để lấy danh sách tất cả bình luận của một userId cụ thể
	@GetMapping("/listComment/{userId}")
	public ResponseEntity<GenericResponseAdmin> getAllCommentsByUserId(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int items, @PathVariable("userId") String userId) {

		// Sử dụng postService để lấy danh sách tất cả bài post của một userId
		Streamable<Object> userCommentsPage = commentService.findAllCommentsByUserId(page, items, userId);

		Optional<User> userOptional = userService.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			long totalComments = commentRepository.countCommentsByUser(user);

			PaginationInfo pagination = new PaginationInfo();
			pagination.setPage(page);
			pagination.setItemsPerPage(items);
			pagination.setCount(totalComments);
			pagination.setPages((int) Math.ceil((double) totalComments / items));

			if (userCommentsPage.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
						.message("No Comments Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
			} else {
				return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
						.message("Retrieved List Comments Successfully").result(userCommentsPage).pagination(pagination)
						.statusCode(HttpStatus.OK.value()).build());
			}
		} else {
			// Xử lý trường hợp không tìm thấy User
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
	}

}
