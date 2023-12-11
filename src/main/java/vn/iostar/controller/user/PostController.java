package vn.iostar.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.repository.PostRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.PostService;
import vn.iostar.service.ShareService;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	PostRepository postRepository;

	@Autowired
	ShareService shareService;

	// Xem chi tiết bài post
	@GetMapping("/{postId}")
	public ResponseEntity<GenericResponse> getPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return postService.getPost(currentUserId, postId);
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<GenericResponse> getPostByUserId(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") String userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(page, size);
		List<PostsResponse> userPosts = postService.findUserPosts(currentUserId, userId, pageable);

		return ResponseEntity.ok(
				GenericResponse.builder().success(true).message("Retrieved user posts successfully and access update")
						.result(userPosts).statusCode(HttpStatus.OK.value()).build());
	}

	// Lấy những bài post liên quan đến mình như: nhóm, bạn bè, cá nhân
	@GetMapping("/get/timeLine")
	public ResponseEntity<GenericResponse> getPostsByUserAndFriendsAndGroups(
			@RequestHeader("Authorization") String authorizationHeader, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return postService.getPostTimelineByUserId(currentUserId, page, size);
	}

	@PutMapping("/update/{postId}")
	public ResponseEntity<Object> updatePost(@ModelAttribute PostUpdateRequest request,
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") Integer postId,
			BindingResult bindingResult) throws Exception {

		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return postService.updatePost(postId, request, currentUserId);

	}

	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deletePost(@RequestHeader("Authorization") String token,
			@PathVariable("postId") Integer postId, @RequestBody String userId) {
		return postService.deletePost(postId, token, userId);

	}

	@PostMapping("/create")
	public ResponseEntity<Object> createUserPost(@ModelAttribute CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return postService.createUserPost(token, requestDTO);
	}

	// Lấy tất cả hình của user đó
	@GetMapping("/user/{userId}/photos")
	public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@PathVariable String userId) {
		return postService.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
	}

	// Lấy 9 hình đầu tiên của user
	@GetMapping("/getPhotos/{userId}")
	public ResponseEntity<Object> getLatestPhotosByUserId(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "9") int size,
			@PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Pageable pageable = PageRequest.of(page, size);
		return postService.findLatestPhotosByUserId(currentUserId, userId, pageable);
	}

	// Xem chi tiết bài share
	@GetMapping("/share/{shareId}")
	public ResponseEntity<GenericResponse> getShare(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("shareId") Integer shareId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return shareService.getShare(currentUserId, shareId);
	}

}
