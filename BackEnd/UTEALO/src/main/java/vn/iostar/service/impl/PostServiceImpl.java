package vn.iostar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;
import vn.iostar.repository.PostRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.PostService;
import vn.iostar.service.UserService;

@Service
public class PostServiceImpl implements PostService {
	@Autowired
	PostRepository postRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserService userService;

	@Autowired
	PostGroupService postGroupService;

	@Override
	public <S extends Post> S save(S entity) {
		return postRepository.save(entity);
	}

	@Override
	public List<Post> findAll() {
		return postRepository.findAll();
	}

	@Override
	public long count() {
		return postRepository.count();
	}

	@Override
	public void deleteById(String id) {
		postRepository.deleteById(id);
	}

	@Override
	public void delete(Post entity) {
		postRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		postRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getPost(String userId) {
		Optional<Post> post = postRepository.findById(userId);
		if (post.isEmpty())
			throw new RuntimeException("Post not found");

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
				.result(new PostResponse(post.get())).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public Optional<Post> findById(String id) {
		return postRepository.findById(id);
	}

	@Override
	public ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request) throws Exception {

		Optional<Post> post = findById(postId);
		if (post.isEmpty())
			throw new Exception("Post doesn't exist");
		if (request.getUpdateAt().after(new Date()))
			throw new Exception("Invalid date");

		post.get().setContent(request.getContent());
		post.get().setLocation(request.getLocation());
		post.get().setUpdateAt(new Date());
		post.get().setPhotos(request.getPhotos());
		save(post.get());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
				.result(new PostResponse(post.get())).statusCode(200).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deletePost(String postId) {
		try {
			Optional<Post> optionalPost = findById(postId);
			// tìm thấy bài post với postId
			if (optionalPost.isPresent()) {
				Post post = optionalPost.get();
				// xóa luôn bài post đó
				postRepository.delete(post);
				return ResponseEntity.ok()
						.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
			}
			// Khi không tìm thấy user với id
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(false, "Invalid arguments!", null, HttpStatus.BAD_REQUEST.value()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(false,
					"An internal server error occurred!", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
		}
	}

	@Override
	public ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO) {
		if (String.valueOf(requestDTO.getPostGroupId()) == null) {
			return ResponseEntity.badRequest().body("Please select post group");
		}
		if (requestDTO.getLocation() == null && requestDTO.getContent() == null && requestDTO.getPhotos() == null) {
			return ResponseEntity.badRequest().body("Please provide all required fields.");
		}

		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		Optional<PostGroup> postGroup = postGroupService.findById(requestDTO.getPostGroupId());
		// Tạo một đối tượng Post từ dữ liệu trong DTO
		Post post = new Post();
		post.setLocation(requestDTO.getLocation());
		post.setContent(requestDTO.getContent());
		post.setPhotos(requestDTO.getPhotos());
		if (user.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found");
		} else {
			post.setUser(user.get());
		}

		if (requestDTO.getPostGroupId() == 0) {
			post.setPostGroup(null);
		} else {
			post.setPostGroup(postGroup.get());
		}

		// Thiết lập các giá trị cố định
		post.setPostTime(new Date());
		post.setUpdateAt(new Date());

		// Tiếp tục xử lý tạo bài đăng
		save(post);
		PostsResponse postsResponse = new PostsResponse(post);
		List<Integer> count = new ArrayList<>();
		postsResponse.setComments(count);
		postsResponse.setLikes(count);

		GenericResponse response = GenericResponse.builder().success(true).message("Post Created Successfully")
				.result(postsResponse).statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	public List<PostsResponse> findUserPosts(String userId) {
		List<Post> userPosts = postRepository.findByUserUserId(userId);
		// Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
		for (Post post : userPosts) {

			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			simplifiedUserPosts.add(postsResponse);
		}
		return simplifiedUserPosts;
	}

	private List<Integer> getIdLikes(List<Like> likes) {
		List<Integer> idComments = new ArrayList<>();
		for (Like like : likes) {
			idComments.add(like.getLikeId());
		}
		return idComments;
	}

	private List<Integer> getIdComment(List<Comment> comments) {
		List<Integer> idComments = new ArrayList<>();
		for (Comment cmt : comments) {
			idComments.add(cmt.getCommentId());
		}
		return idComments;
	}

	@Override
	public Post likePost(String postId, String userId) {
		// Tìm bài post cần like
		Post post = postRepository.findById(postId).orElse(null);
		if (post == null) {
			// Xử lý trường hợp không tìm thấy bài post
			return null;
		}

		// Kiểm tra xem người dùng đã like bài post này chưa
		boolean isLiked = post.getLikes().stream().anyMatch(like -> like.getUser().getUserId() == userId);

		if (!isLiked) {
			// Nếu chưa like, thêm một like mới
			Like like = new Like();
			like.setPost(post);
			// Gán userId vào like
			// Đặt các giá trị khác cho like nếu cần
			post.getLikes().add(like);

			// Cập nhật post
			post = postRepository.save(post);
		} else {
			// Nếu đã like, xử lý việc unlike
			post.getLikes().removeIf(like -> like.getUser().getUserId() == userId);
			// Cập nhật post
			post = postRepository.save(post);
		}

		return post;
	}

	@Override
	public PostsResponse getPost(Post post) {
		PostsResponse postsResponse = new PostsResponse(post);
		postsResponse.setComments(getIdComment(post.getComments()));
		postsResponse.setLikes(getIdLikes(post.getLikes()));
		return postsResponse; 	
	}

}
