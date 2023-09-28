package vn.iostar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.LikeCommentResponse;
import vn.iostar.dto.LikePostResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;
import vn.iostar.service.LikeService;
import vn.iostar.service.PostService;
import vn.iostar.service.UserService;

@Service
public class LikeServiceImpl implements LikeService {

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;

	@Autowired
	CommentService commentService;

	@Autowired
	UserService userService;

	@Override
	public <S extends Like> S save(S entity) {
		return likeRepository.save(entity);
	}

	@Override
	public List<Like> findAll() {
		return likeRepository.findAll();
	}

	@Override
	public <S extends Like> Page<S> findAll(Example<S> example, Pageable pageable) {
		return likeRepository.findAll(example, pageable);
	}

	@Override
	public Optional<Like> findById(Integer id) {
		return likeRepository.findById(id);
	}

	@Override
	public long count() {
		return likeRepository.count();
	}

	@Override
	public void delete(Like entity) {
		likeRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		likeRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getLikeOfPost(int postId) {
		Optional<Post> post = postService.findById(postId);
		if (post.isEmpty())
			throw new RuntimeException("Post not found");
		List<Like> likes = likeRepository.findByPostPostId(postId);
		if (likes.isEmpty())
			throw new RuntimeException("This post has no like");
		List<LikePostResponse> likePostResponses = new ArrayList<>();
		for (Like like : likes) {
			likePostResponses.add(new LikePostResponse(like));
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving like of post successfully")
				.result(likePostResponses).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getCountLikeOfPost(int postId) {
		Optional<Post> post = postService.findById(postId);
		if (post.isEmpty())
			throw new RuntimeException("Post not found");
		List<Like> likes = likeRepository.findByPostPostId(postId);
		if (likes.isEmpty())
			throw new RuntimeException("This post has no like");
		List<LikePostResponse> likePostResponses = new ArrayList<>();
		for (Like like : likes) {
			likePostResponses.add(new LikePostResponse(like));
		}
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving number of likes of Post successfully")
						.result(likePostResponses.size()).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<Object> toggleLikePost(String token, Integer postId) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Post> post = postService.findById(postId);
		if (!post.isPresent()) {
			return ResponseEntity.badRequest().body("Post not found");
		}
		// Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
		Optional<Like> existingLike = findByPostAndUser(post.get(), user.get());

		if (existingLike.isPresent()) {
			// Nếu đã tồn tại, thực hiện xóa
			delete(existingLike.get());
			return ResponseEntity.ok("Like post removed successfully");
		} else {
			// Nếu chưa tồn tại, tạo và lưu Like mới
			Like like = new Like();
			like.setPost(post.get());
			like.setUser(user.get());
			like.setStatus(null); // Cập nhật status nếu cần
			save(like);

			GenericResponse response = GenericResponse.builder().success(true).message("Like Post Successfully").result(
					new LikePostResponse(like.getLikeId(), like.getPost().getPostId(), like.getUser().getUserName()))
					.statusCode(200).build();

			return ResponseEntity.ok(response);
		}
	}

	public Optional<Like> findByPostAndUser(Post post, User user) {
		return likeRepository.findByPostAndUser(post, user);
	}

	@Override
	public ResponseEntity<GenericResponse> getCountLikeOfComment(int commentId) {
		Optional<Comment> comment = commentService.findById(commentId);
		if (comment.isEmpty())
			throw new RuntimeException("Comment not found");
		List<Like> likes = likeRepository.findByCommentCommentId(commentId);
		if (likes.isEmpty())
			throw new RuntimeException("This comment has no like");
		List<LikeCommentResponse> likePostResponses = new ArrayList<>();
		for (Like like : likes) {
			likePostResponses.add(new LikeCommentResponse(like));
		}
		return ResponseEntity.ok(
				GenericResponse.builder().success(true).message("Retrieving number of comments of Post successfully")
						.result(likePostResponses.size()).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getLikeOfComment(int commentId) {
		Optional<Comment> comment = commentService.findById(commentId);
		if (comment.isEmpty())
			throw new RuntimeException("Comment not found");
		List<Like> likes = likeRepository.findByCommentCommentId(commentId);
		if (likes.isEmpty())
			throw new RuntimeException("This comment has no like");
		List<LikeCommentResponse> likePostResponses = new ArrayList<>();
		for (Like like : likes) {
			likePostResponses.add(new LikeCommentResponse(like));
		}
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving like of comment successfully")
						.result(likePostResponses).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<Object> toggleLikeComment(String token, Integer commentId) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Comment> comment = commentService.findById(commentId);
		if (!comment.isPresent()) {
			return ResponseEntity.badRequest().body("Comment not found");
		}
		// Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
		Optional<Like> existingLike = findByCommentAndUser(comment.get(), user.get());

		if (existingLike.isPresent()) {
			// Nếu đã tồn tại, thực hiện xóa
			delete(existingLike.get());
			return ResponseEntity.ok("Like comment removed successfully");
		} else {
			// Nếu chưa tồn tại, tạo và lưu Like mới
			Like like = new Like();
			like.setComment(comment.get());
			like.setUser(user.get());
			like.setStatus(null); // Cập nhật status nếu cần
			save(like);

			GenericResponse response = GenericResponse.builder().success(true).message("Like Comment Successfully")
					.result(new LikeCommentResponse(like.getLikeId(), like.getComment().getCommentId(),
							like.getUser().getUserName()))
					.statusCode(200).build();

			return ResponseEntity.ok(response);
		}
	}

	@Override
	public Optional<Like> findByCommentAndUser(Comment comment, User user) {
		return likeRepository.findByCommentAndUser(comment, user);
	}

	@Override
	public ResponseEntity<Object> checkUserLikePost(String token, Integer postId) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Post> post = postService.findById(postId);
		if (!post.isPresent()) {
			return ResponseEntity.badRequest().body("Post not found");
		}
		// Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
		Optional<Like> existingLike = findByPostAndUser(post.get(), user.get());

		if (existingLike.isPresent()) {
			// Nếu đã tồn tại, trả về true
			GenericResponse response = GenericResponse.builder().success(true).message("Is Liked").result(true)
					.statusCode(200).build();
			return ResponseEntity.ok(response);
		} else {
			GenericResponse response = GenericResponse.builder().success(true).message("Not Like").result(false)
					.statusCode(200).build();
			return ResponseEntity.ok(response);
		}
	}

	@Override
	public ResponseEntity<Object> checkUserLikeComment(String token, Integer commentId) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Comment> comment = commentService.findById(commentId);
		if (!comment.isPresent()) {
			return ResponseEntity.badRequest().body("Comment not found");
		}
		// Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
		Optional<Like> existingLike = findByCommentAndUser(comment.get(), user.get());

		if (existingLike.isPresent()) {
			// Nếu đã tồn tại, trả về true
			GenericResponse response = GenericResponse.builder().success(true).message("Is Liked").result(true)
					.statusCode(200).build();
			return ResponseEntity.ok(response);
		} else {
			GenericResponse response = GenericResponse.builder().success(true).message("Not Like").result(false)
					.statusCode(200).build();
			return ResponseEntity.ok(response);
		}
	}

}
