package vn.iostar.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.contants.RoleName;
import vn.iostar.dto.CommentPostResponse;
import vn.iostar.dto.CommentShareResponse;
import vn.iostar.dto.CommentUpdateRequest;
import vn.iostar.dto.CommentsResponse;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.CreateCommentShareRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PaginationInfo;
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
		List<CommentShareResponse> comments = getCommentsOfShare(shareId);
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
		List<Comment> commentPost = commentRepository
				.findByPostPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(postId);

		List<CommentPostResponse> commentPostResponses = new ArrayList<>();
		for (Comment comment : commentPost) {
			CommentPostResponse cPostResponse = new CommentPostResponse(comment);
			cPostResponse.setLikes(getIdLikes(comment.getLikes()));
			commentPostResponses.add(cPostResponse);
		}
		return commentPostResponses;
	}

	public List<CommentShareResponse> getCommentsOfShare(int shareId) {
		List<Comment> commentPost = commentRepository
				.findByShareShareIdAndCommentReplyIsNullOrderByCreateTimeDesc(shareId);

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
		Optional<Comment> comment = findById(commentId);

		// Lấy comment reply của commentId
		for (Comment directReply : directReplies) {
			CommentPostResponse directReplyResponse = new CommentPostResponse(directReply);
			directReplyResponse.setLikes(getIdLikes(directReply.getLikes()));
			directReplyResponse.setUserOwner(comment.get().getUser().getUserName());
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
		Optional<Comment> comment = findById(commentId);
		// Lấy comment reply của commentId
		for (Comment directReply : directReplies) {
			CommentShareResponse directReplyResponse = new CommentShareResponse(directReply);
			directReplyResponse.setLikes(getIdLikes(directReply.getLikes()));
			directReplyResponse.setUserOwner(comment.get().getUser().getUserName());
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
	public ResponseEntity<GenericResponse> deleteCommentByAdmin(Integer commentId, String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<Comment> optionalComment = findById(commentId);
		Streamable<Object> commentsPage = findAllComments(1, 10);
		// Tìm thấy bài comment với commentId
		if (optionalComment.isPresent()) {
			Comment comment = optionalComment.get();
			commentRepository.delete(comment);
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", commentsPage, HttpStatus.OK.value()));
		}
		// Khi không tìm thấy comment với id
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found comment!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public Streamable<Object> findAllComments(int page, int itemsPerPage) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Page<Comment> commentsPage = commentRepository.findAllByOrderByCreateTimeDesc(pageable);

		Streamable<Object> commentResponsesPage = commentsPage.map(comment -> {
			if (comment.getPost() != null && comment.getPost().getPostId() != 0) {
				CommentPostResponse cPostResponse = new CommentPostResponse(comment);
				cPostResponse.setLikes(getIdLikes(comment.getLikes()));
				return cPostResponse;
			} else if (comment.getShare() != null && comment.getShare().getShareId() != 0) {
				CommentShareResponse cShareResponse = new CommentShareResponse(comment);
				cShareResponse.setLikes(getIdLikes(comment.getLikes()));
				return cShareResponse;
			}
			return null;
		}); // Lọc bất kỳ giá trị null nào nếu có

		return commentResponsesPage;
	}

	@Override
	public ResponseEntity<GenericResponseAdmin> getAllComments(String authorizationHeader, int page, int itemsPerPage) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		Streamable<Object> commentsPage = findAllComments(page, itemsPerPage);
		long totalComments = commentRepository.count();

		PaginationInfo pagination = new PaginationInfo();
		pagination.setPage(page);
		pagination.setItemsPerPage(itemsPerPage);
		pagination.setCount(totalComments);
		pagination.setPages((int) Math.ceil((double) totalComments / itemsPerPage));

		if (commentsPage.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("No Comments Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity
					.ok(GenericResponseAdmin.builder().success(true).message("Retrieved List Comments Successfully")
							.result(commentsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
		}
	}

	// Đếm số lượng comment từng tháng trong năm
	@Override
	public Map<String, Long> countCommentsByMonthInYear() {
		LocalDateTime now = LocalDateTime.now();
		int currentYear = now.getYear();

		// Tạo một danh sách các tháng
		List<Month> months = Arrays.asList(Month.values());
		Map<String, Long> commentCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

		for (Month month : months) {
			LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
			LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

			Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
			Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

			long commentCount = commentRepository.countByCreateTimeBetween(startDateAsDate, endDateAsDate);
			commentCountsByMonth.put(month.toString(), commentCount);
		}

		return commentCountsByMonth;
	}

	// Đếm số lượng comment trong 1 năm
	@Override
	public long countCommentsInOneYearFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusYears(1);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return commentRepository.countByCreateTimeBetween(startDateAsDate, endDateAsDate);
	}

	// Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
	private Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// Chuyển sang giờ kết thức của 1 ngày là 23:59:59
	private Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	private Date getNDaysAgo(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -days);
		return calendar.getTime();
	}

	// Chuyển từ kiểu Comment sang CommentsResponse
	private List<CommentsResponse> mapToCommentsResponseList(List<Comment> comments) {
		List<CommentsResponse> responses = new ArrayList<>();
		for (Comment comment : comments) {
			CommentsResponse postsResponse = new CommentsResponse(comment);
			postsResponse.setLikes(getIdLikes(comment.getLikes()));
			responses.add(postsResponse);
		}
		return responses;
	}

	@Override
	public List<CommentsResponse> getCommentsToday() {
		Date startDate = getStartOfDay(new Date());
		Date endDate = getEndOfDay(new Date());
		List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
		return mapToCommentsResponseList(comments);
	}

	@Override
	public List<CommentsResponse> getCommentsIn7Days() {
		Date startDate = getStartOfDay(getNDaysAgo(6));
		Date endDate = getEndOfDay(new Date());
		List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
		return mapToCommentsResponseList(comments);
	}

	@Override
	public List<CommentsResponse> getCommentsIn1Month() {
		Date startDate = getStartOfDay(getNDaysAgo(30));
		Date endDate = getEndOfDay(new Date());
		List<Comment> comments = commentRepository.findByCreateTimeBetween(startDate, endDate);
		return mapToCommentsResponseList(comments);
	}

	@Override
	public Streamable<Object> findAllCommentsByUserId(int page, int itemsPerPage, String userId) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Page<Comment> commentsPage = commentRepository.findAllByUser_UserIdOrderByCreateTimeDesc(userId, pageable);

		Streamable<Object> commentResponsesPage = commentsPage.map(comment -> {
			if (comment.getPost() != null && comment.getPost().getPostId() != 0) {
				CommentPostResponse cPostResponse = new CommentPostResponse(comment);
				cPostResponse.setLikes(getIdLikes(comment.getLikes()));
				return cPostResponse;
			} else if (comment.getShare() != null && comment.getShare().getShareId() != 0) {
				CommentShareResponse cShareResponse = new CommentShareResponse(comment);
				cShareResponse.setLikes(getIdLikes(comment.getLikes()));
				return cShareResponse;
			}
			return null;
		}); // Lọc bất kỳ giá trị null nào nếu có

		return commentResponsesPage;
	}

	// Đếm số lượng comment của 1 user từng tháng trong năm
	@Override
	public Map<String, Long> countCommentsByUserMonthInYear(String userId) {
		LocalDateTime now = LocalDateTime.now();
		int currentYear = now.getYear();
		
		Optional<User> userOp = userService.findById(userId);
		User user = userOp.get();

		// Tạo một danh sách các tháng
		List<Month> months = Arrays.asList(Month.values());
		Map<String, Long> commentCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

		for (Month month : months) {
			LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
			LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

			Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
			Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

			long commentCount = commentRepository.countByUserAndCreateTimeBetween(user,startDateAsDate, endDateAsDate);
			commentCountsByMonth.put(month.toString(), commentCount);
		}

		return commentCountsByMonth;
	}

	@Override
	public Streamable<Object> findAllCommentsInMonthByUserId(int page, int itemsPerPage, String userId) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Optional<User> user = userService.findById(userId);
		Date startDate = getStartOfDay(getNDaysAgo(30));
		Date endDate = getEndOfDay(new Date());
		Page<Comment> commentsPage = commentRepository.findByUserAndCreateTimeBetween(user.get(),startDate,endDate, pageable);

		Streamable<Object> commentResponsesPage = commentsPage.map(comment -> {
			if (comment.getPost() != null && comment.getPost().getPostId() != 0) {
				CommentPostResponse cPostResponse = new CommentPostResponse(comment);
				cPostResponse.setLikes(getIdLikes(comment.getLikes()));
				return cPostResponse;
			} else if (comment.getShare() != null && comment.getShare().getShareId() != 0) {
				CommentShareResponse cShareResponse = new CommentShareResponse(comment);
				cShareResponse.setLikes(getIdLikes(comment.getLikes()));
				return cShareResponse;
			}
			return null;
		}); // Lọc bất kỳ giá trị null nào nếu có

		return commentResponsesPage;
	}

}
