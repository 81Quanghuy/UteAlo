package vn.iostar.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import vn.iostar.dto.PostsResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostService;

@RestController
@RequestMapping("/api/v1/admin/postManager")
public class PostManagerController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;

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
    public List<PostsResponse> getPosts(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date
    ) {
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
                if (date != null) {
                    return postService.getPostsInMonth(date);
                }
                break;
            default:
                // Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi hoặc một giá trị mặc định
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
            		
            CountDTO postCountDTO = new CountDTO(postCountToDay,postCountInWeek,postCountIn1Month,postCountIn3Month,postCountIn6Month,postCountIn9Month,postCountIn1Year);
            return ResponseEntity.ok(postCountDTO);
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
	
}
