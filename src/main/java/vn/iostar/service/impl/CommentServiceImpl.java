package vn.iostar.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import vn.iostar.dto.CommentPostResponse;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;
import vn.iostar.repository.CommentRepository;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.CommentService;
import vn.iostar.service.PostService;
import vn.iostar.service.UserService;

@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	UserService userService;

	@Autowired
	PostService postService;

	@Autowired
	LikeRepository likeRepository;

	@Override
	public <S extends Comment> S save(S entity) {
		return commentRepository.save(entity);
	}

	@Override
	public Page<Comment> findAll(Pageable pageable) {
		return commentRepository.findAll(pageable);
	}

	@Override
	public List<Comment> findAll() {
		return commentRepository.findAll();
	}

	@Override
	public <S extends Comment> Page<S> findAll(Example<S> example, Pageable pageable) {
		return commentRepository.findAll(example, pageable);
	}

	@Override
	public Optional<Comment> findById(Integer id) {
		return commentRepository.findById(id);
	}

	@Override
	public long count() {
		return commentRepository.count();
	}

	@Override
	public void delete(Comment entity) {
		commentRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		commentRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getCommentOfPost(int postId) {
		Optional<Post> post = postService.findById(postId);
		if (post.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("Post not found").result(false)
					.statusCode(HttpStatus.OK.value()).build());
		List<CommentPostResponse> comments = getCommentsOfPost(postId);
		if (comments.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("This post has no comment")
					.result(false).statusCode(HttpStatus.OK.value()).build());
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
						.result(comments).statusCode(HttpStatus.OK.value()).build());
	}
	
	public List<CommentPostResponse> getCommentsOfPost(int postId) {
		List<Comment> commentPost = commentRepository.findByPostPostIdOrderByCreateTimeDesc(postId);
		
		List<CommentPostResponse> commentPostResponses = new ArrayList<>();
		for(Comment comment : commentPost) {
			CommentPostResponse cPostResponse = new CommentPostResponse(comment);
			cPostResponse.setLikes(getIdLikes(comment.getLikes()));
			commentPostResponses.add(cPostResponse);
		}
		return commentPostResponses;
	}
	
	private List<Integer> getIdLikes(List<Like> likes) {
		List<Integer> idComments = new ArrayList<>();
		for (Like like : likes) {
			idComments.add(like.getLikeId());
		}
		return idComments;
	}

	@Override
	public ResponseEntity<GenericResponse> getCountCommentOfPost(int postId) {
		Optional<Post> post = postService.findById(postId);
		if (post.isEmpty())
			throw new RuntimeException("Post not found");
		List<Comment> comments = commentRepository.findByPostPostIdOrderByCreateTimeDesc(postId);
		if (comments.isEmpty())
			throw new RuntimeException("This post has no comment");
		List<CommentPostResponse> commentPostResponses = new ArrayList<>();
		for (Comment comment : comments) {
			commentPostResponses.add(new CommentPostResponse(comment));
		}
		return ResponseEntity.ok(
				GenericResponse.builder().success(true).message("Retrieving number of comments of Post successfully")
						.result(commentPostResponses.size()).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Post> post = postService.findById(requestDTO.getPostId());
		if (!post.isPresent()) {
			return ResponseEntity.badRequest().body("Post not found");
		}

		Comment comment = new Comment();
		comment.setPost(post.get());
		comment.setCreateTime(new Date());
		comment.setContent(requestDTO.getContent());
		comment.setPhotos("");
		comment.setUser(user.get());
		save(comment);
		GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
				.result(new CommentPostResponse(comment.getCommentId(), comment.getContent(), comment.getCreateTime(),
						comment.getPhotos(), comment.getUser().getUserName(), comment.getPost().getPostId(),comment.getUser().getProfile().getAvatar(),comment.getUser().getUserId()))
				.statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deleteCommentOfPost(Integer commentId) {

		Optional<Comment> optionalComment = findById(commentId);

		// tìm thấy bài comment với commentId
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();

			// Xóa tất cả các like liên quan đến bình luận này
			likeRepository.deleteByCommentCommentId(comment.getCommentId());

			// xóa luôn bài comment đó
			commentRepository.delete(comment);
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		}
		// Khi không tìm thấy comment với id
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found comment!", null, HttpStatus.NOT_FOUND.value()));
		}

	}

}
