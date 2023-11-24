package vn.iostar.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import vn.iostar.entity.Post;
import vn.iostar.entity.User;
import vn.iostar.repository.PostRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.PostService;
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

	// Xem chi tiết bài post
	@GetMapping("/{postId}")
	public ResponseEntity<GenericResponse> getPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<Post> post = postService.findById(postId);
		PostsResponse userPosts = postService.getPost(post.get());
		if (post.isEmpty()) {
			throw new RuntimeException("Post not found.");
		} else if (currentUserId.equals(post.get().getUser().getUserId())) {
			return ResponseEntity.ok(
					GenericResponse.builder().success(true).message("Retrieving post successfully and access update")
							.result(userPosts).statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieving post successfully and access update denied").result(userPosts)
					.statusCode(HttpStatus.OK.value()).build());
		}
	}

	// Lấy những bài post của mình
	@GetMapping("/{userId}/post")
	public ResponseEntity<GenericResponse> getUserPosts(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		List<PostsResponse> userPosts = postService.findUserPosts(userId);

		if (!userId.equals(currentUserId)) {
			throw new RuntimeException("User not found.");
		} else if (userPosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No posts found for this user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else if (!currentUserId.equals(userId)) {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieved user posts successfully and access update denied").result(userPosts)
					.statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieved user posts successfully and access update").result(userPosts)
					.statusCode(HttpStatus.OK.value()).build());
		}
	}


	// Lấy những bài post liên quan đến mình như: nhóm, bạn bè, cá nhân
	@GetMapping("/{userId}/posts")
	public ResponseEntity<GenericResponse> getPostsByUserAndFriendsAndGroups(
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("userId") String userId) {
		
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		List<PostsResponse> userPosts = postService.findPostsByUserAndFriendsAndGroupsOrderByPostTimeDesc(user.get());

		if (!userId.equals(currentUserId)) {
			throw new RuntimeException("User not found.");
		} else if (userPosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No posts found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved posts successfully")
					.result(userPosts).statusCode(HttpStatus.OK.value()).build());
	}

	@PutMapping("/update/{postId}")
	public ResponseEntity<Object> updateUser(@ModelAttribute PostUpdateRequest request,
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") Integer postId,
			BindingResult bindingResult) throws Exception {

		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

		return postService.updatePost(postId, request, currentUserId);

	}

	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String token,
			@PathVariable("postId") Integer postId, @RequestBody String userId) {
		return postService.deletePost(postId, token, userId);

	}

	@PostMapping("/create")
	public ResponseEntity<Object> createPost(@ModelAttribute CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return postService.createUserPost(token, requestDTO);
	}

	@GetMapping("/user/{userId}/photos")
	public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@PathVariable String userId) {
		return postService.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
	}

	@GetMapping("/user/{userId}/latest-photos")
	public Page<String> getLatestPhotosByUserId(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "9") int size, @PathVariable("userId") String userId) {
		return postService.findLatestPhotosByUserId(userId, page, size);
	}

}
