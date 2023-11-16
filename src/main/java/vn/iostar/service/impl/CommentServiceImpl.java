package vn.iostar.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.contants.RoleName;
import vn.iostar.dto.CommentPostResponse;
import vn.iostar.dto.CommentShareResponse;
import vn.iostar.dto.CommentUpdateRequest;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.CreateCommentShareRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.ReplyCommentPostRequestDTO;
import vn.iostar.dto.ReplyCommentShareRequestDTO;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.Share;
import vn.iostar.entity.User;
import vn.iostar.repository.CommentRepository;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.CommentService;
import vn.iostar.service.PostService;
import vn.iostar.service.ShareService;
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
	ShareService shareService;

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
	
	@Override
	public ResponseEntity<GenericResponse> getCommentOfShare(int shareId) {
		Optional<Share> share = shareService.findById(shareId);
		if (share.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("Share not found").result(false)
					.statusCode(HttpStatus.OK.value()).build());
		List<CommentShareResponse> comments =  getCommentsOfShare(shareId);
		if (comments.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("This share has no comment")
					.result(false).statusCode(HttpStatus.OK.value()).build());
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving comment of share post successfully")
						.result(comments).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getCommentReplyOfComment(int commentId) {
		Optional<Comment> comment = findById(commentId);
		if (comment.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("Comment not found").result(false)
					.statusCode(HttpStatus.OK.value()).build());
		List<CommentPostResponse> comments = getCommentsOfComment(commentId);
		if (comments.isEmpty())
			return ResponseEntity
					.ok(GenericResponse.builder().success(false).message("This comment has no comment reply")
							.result(false).statusCode(HttpStatus.OK.value()).build());
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving comment of post successfully")
						.result(comments).statusCode(HttpStatus.OK.value()).build());
	}
	
	@Override
	public ResponseEntity<GenericResponse> getCommentReplyOfCommentShare(int commentId) {
		Optional<Comment> comment = findById(commentId);
		if (comment.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(false).message("Comment not found").result(false)
					.statusCode(HttpStatus.OK.value()).build());
		List<CommentShareResponse> comments = getCommentsOfCommentShare(commentId);
		if (comments.isEmpty())
			return ResponseEntity
					.ok(GenericResponse.builder().success(false).message("This comment has no comment reply")
							.result(false).statusCode(HttpStatus.OK.value()).build());
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Retrieving comment of share post successfully")
						.result(comments).statusCode(HttpStatus.OK.value()).build());
	}

	public List<CommentPostResponse> getCommentsOfPost(int postId) {
		List<Comment> commentPost = commentRepository.findByPostPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(postId);

		List<CommentPostResponse> commentPostResponses = new ArrayList<>();
		for (Comment comment : commentPost) {
			CommentPostResponse cPostResponse = new CommentPostResponse(comment);
			cPostResponse.setLikes(getIdLikes(comment.getLikes()));
			commentPostResponses.add(cPostResponse);
		}
		return commentPostResponses;
	}
	
	public List<CommentShareResponse> getCommentsOfShare(int shareId) {
		List<Comment> commentPost = commentRepository.findByShareShareIdAndCommentReplyIsNullOrderByCreateTimeDesc(shareId);

		List<CommentShareResponse> commentPostResponses = new ArrayList<>();
		for (Comment comment : commentPost) {
			CommentShareResponse cPostResponse = new CommentShareResponse(comment);
			cPostResponse.setLikes(getIdLikes(comment.getLikes()));
			commentPostResponses.add(cPostResponse);
		}
		return commentPostResponses;
	}

	public List<CommentPostResponse> getCommentsOfComment(int commentId) {
	    List<CommentPostResponse> commentPostResponses = new ArrayList<>();

	    // Tìm các comment reply trực tiếp cho commentId
	    List<Comment> directReplies = commentRepository.findCommentRepliesByCommentIdOrderByCreateTimeDesc(commentId);

	    // Lấy comment reply của commentId
	    for (Comment directReply : directReplies) {
	        CommentPostResponse directReplyResponse = new CommentPostResponse(directReply);
	        directReplyResponse.setLikes(getIdLikes(directReply.getLikes()));
	        commentPostResponses.add(directReplyResponse);

	        // Tìm các comment reply cho directReply
	        List<CommentPostResponse> subReplies = getCommentsOfComment(directReply.getCommentId());

	        // Thêm tất cả các comment reply của directReply
	        commentPostResponses.addAll(subReplies);
	    }

	    return commentPostResponses;
	}
	
	public List<CommentShareResponse> getCommentsOfCommentShare(int commentId) {
	    List<CommentShareResponse> commentPostResponses = new ArrayList<>();

	    // Tìm các comment reply trực tiếp cho commentId
	    List<Comment> directReplies = commentRepository.findCommentRepliesByCommentIdOrderByCreateTimeDesc(commentId);

	    // Lấy comment reply của commentId
	    for (Comment directReply : directReplies) {
	    	CommentShareResponse directReplyResponse = new CommentShareResponse(directReply);
	        directReplyResponse.setLikes(getIdLikes(directReply.getLikes()));
	        commentPostResponses.add(directReplyResponse);

	        // Tìm các comment reply cho directReply
	        List<CommentShareResponse> subReplies = getCommentsOfCommentShare(directReply.getCommentId());

	        // Thêm tất cả các comment reply của directReply
	        commentPostResponses.addAll(subReplies);
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
		List<Comment> comments = commentRepository.findByPostPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(postId);
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
		comment.setUpdateAt(new Date());
		comment.setContent(requestDTO.getContent());
		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				comment.setPhotos("");
			} else {

				comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}

		comment.setUser(user.get());
		save(comment);
		GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
				.result(new CommentPostResponse(comment.getCommentId(), comment.getContent(), comment.getCreateTime(),
						comment.getPhotos(), comment.getUser().getUserName(), comment.getPost().getPostId(),
						comment.getUser().getProfile().getAvatar(), comment.getUser().getUserId()))
				.statusCode(200).build();

		return ResponseEntity.ok(response);
	}
	
	@Override
	public ResponseEntity<Object> createCommentShare(String token, CreateCommentShareRequestDTO requestDTO) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Share> share = shareService.findById(requestDTO.getShareId());
		if (!share.isPresent()) {
			return ResponseEntity.badRequest().body("Share not found");
		}

		Comment comment = new Comment();
		comment.setShare(share.get());
		comment.setCreateTime(new Date());
		comment.setUpdateAt(new Date());
		comment.setContent(requestDTO.getContent());
		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				comment.setPhotos("");
			} else {

				comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}
		comment.setUser(user.get());
		save(comment);
		GenericResponse response = GenericResponse.builder().success(true).message("Comment Share Successfully")
				.result(new CommentShareResponse(comment.getCommentId(), comment.getContent(), comment.getCreateTime(),
						comment.getPhotos(), comment.getUser().getUserName(), comment.getShare().getShareId(),
						comment.getUser().getProfile().getAvatar(), comment.getUser().getUserId()))
				.statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Object> replyCommentPost(String token, ReplyCommentPostRequestDTO requestDTO) {
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
		Optional<Comment> commentReply = findById(requestDTO.getCommentId());
		if (!commentReply.isPresent()) {
			return ResponseEntity.badRequest().body("Comment not found");
		}

		Comment comment = new Comment();
		comment.setPost(post.get());
		comment.setCreateTime(new Date());
		comment.setUpdateAt(new Date());
		comment.setContent(requestDTO.getContent());
		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				comment.setPhotos("");
			} else {

				comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}
		comment.setUser(user.get());
		comment.setCommentReply(commentReply.get());
		save(comment);
		GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
				.result(new CommentPostResponse(comment.getCommentId(), comment.getContent(), comment.getCreateTime(),
						comment.getPhotos(), comment.getUser().getUserName(), comment.getPost().getPostId(),
						comment.getUser().getProfile().getAvatar(), comment.getUser().getUserId()))
				.statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	
	@Override
	public ResponseEntity<Object> replyCommentShare(String token, ReplyCommentShareRequestDTO requestDTO) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (!user.isPresent()) {
			return ResponseEntity.badRequest().body("User not found");
		}
		Optional<Share> share = shareService.findById(requestDTO.getShareId());
		if (!share.isPresent()) {
			return ResponseEntity.badRequest().body("Share post not found");
		}
		Optional<Comment> commentReply = findById(requestDTO.getCommentId());
		if (!commentReply.isPresent()) {
			return ResponseEntity.badRequest().body("Comment not found");
		}

		Comment comment = new Comment();
		comment.setShare(share.get());
		comment.setCreateTime(new Date());
		comment.setUpdateAt(new Date());
		comment.setContent(requestDTO.getContent());
		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				comment.setPhotos("");
			} else {

				comment.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}
		comment.setUser(user.get());
		comment.setCommentReply(commentReply.get());
		save(comment);
		GenericResponse response = GenericResponse.builder().success(true).message("Comment Share Post Successfully")
				.result(new CommentShareResponse(comment.getCommentId(), comment.getContent(), comment.getCreateTime(),
						comment.getPhotos(), comment.getUser().getUserName(), comment.getShare().getShareId(),
						comment.getUser().getProfile().getAvatar(), comment.getUser().getUserId()))
				.statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	
	@Override
	public ResponseEntity<Object> updateComment(Integer commentId, CommentUpdateRequest request, String currentUserId)
			throws Exception {

		Optional<Comment> commentOp = findById(commentId);
		if (commentOp.isEmpty())
			throw new Exception("Comment doesn't exist");
		Comment comment = commentOp.get();
		if (!currentUserId.equals(commentOp.get().getUser().getUserId()))
			throw new Exception("Update denied");
		comment.setContent(request.getContent());
		try {
			if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
				comment.setPhotos("");
			} else if (request.getPhotos().equals(commentOp.get().getPhotos())) {
				comment.setPhotos(commentOp.get().getPhotos());
			} else {
				comment.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}
		comment.setUpdateAt(new Date());
		save(comment);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
				.statusCode(200).build());
	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deleteCommentOfPost(Integer commentId) {

		Optional<Comment> optionalComment = findById(commentId);

		// tìm thấy bài comment với commentId
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
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
	
	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deleteCommentByAdmin(Integer commentId,String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if(!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<Comment> optionalComment = findById(commentId);
		// Tìm thấy bài comment với commentId
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
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

	@Override
	public List<CommentPostResponse> findAllComments() {
		List<Comment> comments = commentRepository.findAllAndCommentReplyIsNullByOrderByCreateTimeDesc();

		List<CommentPostResponse> commentResponses = new ArrayList<>();
		for (Comment comment : comments) {
			CommentPostResponse cPostResponse = new CommentPostResponse(comment);
			cPostResponse.setLikes(getIdLikes(comment.getLikes()));
			commentResponses.add(cPostResponse);
		}
		return commentResponses;
	}

	@Override
	public ResponseEntity<GenericResponse> getAllComments(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if(!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		List<CommentPostResponse> comments = findAllComments();
		if (comments.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No Comments Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		return ResponseEntity.ok(
				GenericResponse.builder().success(true).message("Retrieved List Comments Successfully")
						.result(comments).statusCode(HttpStatus.OK.value()).build());
	}

}
