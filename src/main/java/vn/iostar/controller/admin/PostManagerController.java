package vn.iostar.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import vn.iostar.dto.CountDTO;
import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PaginationInfo;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.User;
import vn.iostar.repository.PostRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostService;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/admin/postManager")
public class PostManagerController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostRepository postRepository;

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;

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

	// Thống kê bài post trong ngày hôm nay
	// Thống kê bài post trong 1 ngày
	// Thống kê bài post trong 7 ngày
	// Thống kê bài post trong 1 tháng
	@GetMapping("/filterByDate")
	public List<PostsResponse> getPosts(@RequestParam(required = false) String action,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		switch (action != null ? action.toLowerCase() : "") {
		case "today":
			return postService.getPostsToday();
		case "day":
			if (date != null) {
				return postService.getPostsInDay(date);
			}
			break;
		case "7days":
			return postService.getPostsIn7Days();
		case "month":
			return postService.getPostsIn1Month();
		default:
			// Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
			// hoặc một giá trị mặc định
			break;
		}
		// Trả về null hoặc danh sách rỗng tùy theo logic của bạn
		return null;
	}

	// Đếm số lượng bài post
	@GetMapping("/countPost")
	public ResponseEntity<CountDTO> countPostsToday() {
		try {
			long postCountToDay = postService.countPostsToday();
			long postCountInWeek = postService.countPostsInWeek();
			long postCountIn1Month = postService.countPostsInMonthFromNow();
			long postCountIn3Month = postService.countPostsInThreeMonthsFromNow();
			long postCountIn6Month = postService.countPostsInSixMonthsFromNow();
			long postCountIn9Month = postService.countPostsInNineMonthsFromNow();
			long postCountIn1Year = postService.countPostsInOneYearFromNow();

			CountDTO postCountDTO = new CountDTO(postCountToDay, postCountInWeek, postCountIn1Month, postCountIn3Month,
					postCountIn6Month, postCountIn9Month, postCountIn1Year);
			return ResponseEntity.ok(postCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng bài post của user từng tháng trong năm
	@GetMapping("/countPostsByMonthInYear/{userId}")
	public ResponseEntity<Map<String, Long>> countPostsByUserMonthInYear(@PathVariable("userId") String userId) {
		try {
			Map<String, Long> postCountsByMonth = postService.countPostsByUserMonthInYear(userId);
			return ResponseEntity.ok(postCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng bài post từng tháng trong năm
	@GetMapping("/countPostsByMonthInYear")
	public ResponseEntity<Map<String, Long>> countPostsByMonthInYear() {
		try {
			Map<String, Long> postCountsByMonth = postService.countPostsByMonthInYear();
			return ResponseEntity.ok(postCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Cập nhật controller để lấy danh sách tất cả bài post của một userId cụ thể
	@GetMapping("/listPost/{userId}")
	public ResponseEntity<GenericResponseAdmin> getAllPostsByUserId(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int items, @PathVariable("userId") String userId) {

		// Sử dụng postService để lấy danh sách tất cả bài post của một userId
		Page<PostsResponse> userPostsPage = postService.findAllPostsByUserId(page, items, userId);

		Optional<User> userOptional = userService.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			long totalPosts = postRepository.countPostsByUser(user);

			PaginationInfo pagination = new PaginationInfo();
			pagination.setPage(page);
			pagination.setItemsPerPage(items);
			pagination.setCount(totalPosts);
			pagination.setPages((int) Math.ceil((double) totalPosts / items));

			if (userPostsPage.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
						.message("No Posts Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
			} else {
				return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
						.message("Retrieved List Posts Successfully").result(userPostsPage).pagination(pagination)
						.statusCode(HttpStatus.OK.value()).build());
			}
		} else {
			// Xử lý trường hợp không tìm thấy User
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
	}

	// Cập nhật controller để lấy danh sách tất cả bài post của một userId trong 1
	// tháng
	@GetMapping("/listPostInMonthPage/{userId}")
	public ResponseEntity<GenericResponseAdmin> findAllPostsInMonthByUserId(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "5") int items, @PathVariable("userId") String userId) {

		// Sử dụng postService để lấy danh sách tất cả bài post của một userId
		Page<PostsResponse> userPostsPage = postService.findAllPostsInMonthByUserId(page, items, userId);

		Optional<User> userOptional = userService.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			long totalPosts = postRepository.countPostsByUser(user);

			PaginationInfo pagination = new PaginationInfo();
			pagination.setPage(page);
			pagination.setItemsPerPage(items);
			pagination.setCount(totalPosts);
			pagination.setPages((int) Math.ceil((double) totalPosts / items));

			if (userPostsPage.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
						.message("No Posts Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
			} else {
				return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
						.message("Retrieved List Posts Successfully").result(userPostsPage).pagination(pagination)
						.statusCode(HttpStatus.OK.value()).build());
			}
		} else {
			// Xử lý trường hợp không tìm thấy User
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
	}

	// Cập nhật controller để lấy danh sách tất cả bài post của một userId trong 1
	// tháng không phân trang
	@GetMapping("/listPostInMonth/{userId}")
	public ResponseEntity<GenericResponseAdmin> findAllPostsInMonthByUserId(@RequestParam(defaultValue = "1") int page,
			@PathVariable("userId") String userId) {

		Optional<User> userOptional = userService.findById(userId);
		if (userOptional.isPresent()) {
			User user = userOptional.get();

			// Tính toán số lượng bài đăng của người dùng trong tháng
			long totalPosts = postRepository.countPostsByUser(user);

			// Kiểm tra nếu totalShares là 0, trả về result rỗng
			if (totalPosts == 0) {
				PaginationInfo pagination = new PaginationInfo();
				pagination.setPage(page);
				pagination.setItemsPerPage(0); // Số lượng mục trên trang là 0 khi không có bài share
				pagination.setCount(0);
				pagination.setPages(0);

				return ResponseEntity.ok(GenericResponseAdmin.builder().success(true).message("No Posts Found for User")
						.result("").pagination(pagination).statusCode(HttpStatus.OK.value()).build());
			}

			// Sử dụng totalPosts cho items để lấy tất cả bài đăng trong một trang
			Page<PostsResponse> userPostsPage = postService.findAllPostsInMonthByUserId(page, (int) totalPosts, userId);

			PaginationInfo pagination = new PaginationInfo();
			pagination.setPage(page);
			pagination.setItemsPerPage((int) totalPosts);
			pagination.setCount(totalPosts);
			pagination.setPages(1); // Chỉ có 1 trang

			if (userPostsPage.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
						.message("No Posts Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
			} else {
				return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
						.message("Retrieved List Posts Successfully").result(userPostsPage).pagination(pagination)
						.statusCode(HttpStatus.OK.value()).build());
			}
		} else {
			// Xử lý trường hợp không tìm thấy User
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("User Not Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
	}

}
