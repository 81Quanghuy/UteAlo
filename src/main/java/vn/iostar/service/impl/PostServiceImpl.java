package vn.iostar.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;
import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.FilesOfGroupDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PaginationInfo;
import vn.iostar.dto.PhoToResponse;
import vn.iostar.dto.PhotosOfGroupDTO;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;
import vn.iostar.repository.CommentRepository;
import vn.iostar.repository.LikeRepository;
import vn.iostar.repository.PostRepository;
import vn.iostar.repository.ShareRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
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

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	ShareRepository shareRepository;

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
	public void deleteById(Integer id) {
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
	public ResponseEntity<GenericResponse> getPost(String currentId, Integer postId) {
		Optional<Post> post = postRepository.findById(postId);
		if (post.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(null).message("not found post").result(null)
					.statusCode(HttpStatus.NOT_FOUND.value()).build());

		PostsResponse postsResponse = new PostsResponse(post.get(), currentId);
		postsResponse.setComments(getIdComment(post.get().getComments()));
		postsResponse.setLikes(getIdLikes(post.get().getLikes()));

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
				.result(postsResponse).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public Optional<Post> findById(Integer id) {
		return postRepository.findById(id);
	}

	@Override
	public ResponseEntity<Object> updatePost(Integer postId, PostUpdateRequest request, String currentUserId)
			throws Exception {

		List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

		Optional<Post> postOp = findById(postId);
		if (postOp.isEmpty()) {
			throw new Exception("Post doesn't exist");
		}
		Post post = postOp.get();
		if (!currentUserId.equals(postOp.get().getUser().getUserId())) {
			throw new Exception("Update denied");
		}
		post.setContent(request.getContent());
		post.setLocation(request.getLocation());
		post.setPrivacyLevel(request.getPrivacyLevel());
		post.setUpdateAt(new Date());
		try {
			if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
				post.setPhotos(request.getPhotoUrl());
			} else {
				post.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
			}

			if (request.getFiles() == null || request.getFiles().getContentType() == null) {
				post.setFiles(request.getFileUrl());
			} else {
				String fileExtension = StringUtils.getFilenameExtension(request.getFiles().getOriginalFilename());
				if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
					post.setFiles(cloudinaryService.uploadFile(request.getFiles()));
				} else {
					throw new IllegalArgumentException("Not support for this file.");
				}
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}
		save(post);
		PostsResponse postResponse = new PostsResponse(post);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
				.result(postResponse).statusCode(200).build());
	}

	// Xóa bài post của mình
	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deletePost(Integer postId, String token, String userId) {

		String jwt = token.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(jwt);
		if (!currentUserId.equals(userId.replaceAll("^\"|\"$", ""))) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
		}
		Optional<Post> optionalPost = findById(postId);
		// tìm thấy bài post với postId
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();

			postRepository.delete(post);

			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		}
		// Khi không tìm thấy bài post với id
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	// Admin xóa bài post trong hệ thống
	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deletePostByAdmin(Integer postId, String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Delete denied!").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<Post> optionalPost = findById(postId);
		Page<PostsResponse> userPostsPage = findAllPosts(1, 10);
		// tìm thấy bài post với postId
		if (optionalPost.isPresent()) {
			Post post = optionalPost.get();
			postRepository.delete(post);
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", userPostsPage, HttpStatus.OK.value()));
		}
		// Khi không tìm thấy bài post với id
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found post!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	/**
	 * @param token
	 * @param requestDTO
	 * @return
	 */
	@Override
	public ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO) {

		List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

		if (requestDTO.getLocation() == null && requestDTO.getContent() == null) {
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
		post.setPrivacyLevel(requestDTO.getPrivacyLevel());

		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				post.setPhotos("");
			} else {
				post.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
			if (requestDTO.getFiles() == null || requestDTO.getFiles().getContentType() == null) {
				post.setFiles("");
			} else {
				String fileExtension = StringUtils.getFilenameExtension(requestDTO.getFiles().getOriginalFilename());
				if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
					post.setFiles(cloudinaryService.uploadFile(requestDTO.getFiles()));
				} else {
					throw new IllegalArgumentException("Not support for this file.");
				}
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}

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

	// lấy những bài post của user theo UserId có privacyLevel là PUBLIC Or FRIENDS
	// OrderBy PostTime Desc
	@Override
	public List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable) {
		List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS);
		if (currentUserId.equals(userId))
			privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS, PrivacyLevel.PRIVATE);

		List<Post> userPosts = postRepository.findByUserUserIdAndPrivacyLevelInOrderByPostTimeDesc(userId,
				privacyLevels, pageable);
		// Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
		for (Post post : userPosts) {
			PostsResponse postsResponse = new PostsResponse(post, currentUserId);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			simplifiedUserPosts.add(postsResponse);
		}
		return simplifiedUserPosts;
	}

	// Lấy những bài post của cá nhân

//	public List<PostsResponse> findUserPostsByUserIdToken(String currentUserId, Pageable pageable) {
//		List<PostsResponse> userPosts = postReposi(currentUserId, pageable);
//		// Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
//		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
//		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
//		for (Post post : userPosts) {
//			PostsResponse postsResponse = new PostsResponse(post);
//			postsResponse.setComments(getIdComment(post.getComments()));
//			postsResponse.setLikes(getIdLikes(post.getLikes()));
//			simplifiedUserPosts.add(postsResponse);
//		}
//		return simplifiedUserPosts;
//	}
	// Lấy tất cả bài post trong hệ thống
	@Override
	public Page<PostsResponse> findAllPosts(int page, int itemsPerPage) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Page<Post> userPostsPage = postRepository.findAllByOrderByPostTimeDesc(pageable);

		return userPostsPage.map(post -> {
			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			return postsResponse;
		});
	}

	@Override
	public ResponseEntity<GenericResponseAdmin> getAllPosts(String authorizationHeader, int page, int itemsPerPage) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		Page<PostsResponse> userPostsPage = findAllPosts(page, itemsPerPage);
		long totalPosts = postRepository.count();

		PaginationInfo pagination = new PaginationInfo();
		pagination.setPage(page);
		pagination.setItemsPerPage(itemsPerPage);
		pagination.setCount(totalPosts);
		pagination.setPages((int) Math.ceil((double) totalPosts / itemsPerPage));

		if (userPostsPage.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("No Posts Found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity
					.ok(GenericResponseAdmin.builder().success(true).message("Retrieved List Posts Successfully")
							.result(userPostsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
		}
	}

	// // Lấy những bài post của nhóm
	@Override
	public List<PostsResponse> findPostGroupPosts(String currentId, Integer postGroupId, Pageable pageable) {
		List<Post> groupPosts = postRepository.findByPostGroupPostGroupIdOrderByPostTimeDesc(postGroupId, pageable);
		List<PostsResponse> simplifiedGroupPosts = new ArrayList<>();
		for (Post post : groupPosts) {
			PostsResponse postsResponse = new PostsResponse(post, currentId);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			simplifiedGroupPosts.add(postsResponse);
		}
		return simplifiedGroupPosts;
	}

	// Lấy những bài post của nhóm
	@Override
	public ResponseEntity<GenericResponse> getGroupPosts(String currentId, Integer postGroupId, Integer page,
			Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		List<PostsResponse> groupPosts = findPostGroupPosts(currentId, postGroupId, pageable);
		if (groupPosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No posts found for this group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Retrieved group posts successfully")
							.result(groupPosts).statusCode(HttpStatus.OK.value()).build());
		}
	}

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@Override
	public List<PostsResponse> findGroupPosts(String currentUserId, Pageable pageable) {
		List<Post> groupPosts = postRepository.findAllPostsInUserGroups(currentUserId, pageable);
		List<PostsResponse> simplifiedGroupPosts = new ArrayList<>();
		for (Post post : groupPosts) {
			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			simplifiedGroupPosts.add(postsResponse);
		}
		return simplifiedGroupPosts;
	}

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@Override
	public ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable)
			throws RuntimeException {
		List<PostsResponse> groupPosts = findGroupPosts(currentUserId, pageable);
		if (currentUserId.isEmpty())
			throw new RuntimeException("User not found.");
		else {
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Retrieved group posts successfully")
							.result(groupPosts).statusCode(HttpStatus.OK.value()).build());
		}
	}

	// Lấy những bài post liên quan đến user như cá nhân, nhóm, bạn bè
	@Override
	public ResponseEntity<GenericResponse> getPostTimelineByUserId(String userId, int page, int size)
			throws RuntimeException {
		Optional<User> user = userService.findById(userId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("User not found.").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		PageRequest pageable = PageRequest.of(page, size);
		List<Post> listPost = postRepository.findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(userId, pageable);
		// Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
		for (Post post : listPost) {
			PostsResponse postsResponse = new PostsResponse(post, userId);
			if (post.getComments() != null && !post.getComments().isEmpty()) {
				postsResponse.setComments(getIdComment(post.getComments()));
			} else {
				postsResponse.setComments(new ArrayList<>());
			}
			if (post.getLikes() != null && !post.getLikes().isEmpty()) {
				postsResponse.setLikes(getIdLikes(post.getLikes()));
			} else {
				postsResponse.setLikes(new ArrayList<>());
			}
			simplifiedUserPosts.add(postsResponse);
		}

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved user posts successfully")
				.result(simplifiedUserPosts).statusCode(HttpStatus.OK.value()).build());
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
	public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId) {
		return postRepository.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
	}

	// Thống kê bài post trong ngày hôm nay
	@Override
	public List<PostsResponse> getPostsToday() {
		Date startDate = getStartOfDay(new Date());
		Date endDate = getEndOfDay(new Date());
		List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
		return mapToPostsResponseList(posts);
	}

	// Thống kê bài post trong 1 ngày
	@Override
	public List<PostsResponse> getPostsInDay(Date day) {
		Date startDate = getStartOfDay(day);
		Date endDate = getEndOfDay(day);
		List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
		return mapToPostsResponseList(posts);
	}

	// Thống kê bài post trong 7 ngày
	@Override
	public List<PostsResponse> getPostsIn7Days() {
		Date startDate = getStartOfDay(getNDaysAgo(6));
		Date endDate = getEndOfDay(new Date());
		List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
		return mapToPostsResponseList(posts);
	}

	// Thống kê bài post trong 1 tháng
	@Override
	public List<PostsResponse> getPostsInMonth(Date month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(month);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = getEndOfDay(calendar.getTime());

		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		Date endDate = getEndOfDay(calendar.getTime());

		List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
		return mapToPostsResponseList(posts);
	}

	// Chuyển từ kiểu Post sang PostsResponse
	private List<PostsResponse> mapToPostsResponseList(List<Post> posts) {
		List<PostsResponse> responses = new ArrayList<>();
		for (Post post : posts) {
			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			responses.add(postsResponse);
		}
		return responses;
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

	// Đếm số lượng bài post trong ngày hôm nay
	@Override
	public long countPostsToday() {
		Date startDate = getStartOfDay(new Date());
		Date endDate = getEndOfDay(new Date());
		return postRepository.countByPostTimeBetween(startDate, endDate);
	}

	// Đếm số lượng bài post trong 7 ngày
	@Override
	public long countPostsInWeek() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
		Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		return postRepository.countByPostTimeBetween(startDate, endDate);
	}

	// Đếm số lượng bài post trong 1 tháng
	@Override
	public long countPostsInMonthFromNow() {
		// Lấy thời gian hiện tại
		LocalDateTime now = LocalDateTime.now();

		// Thời gian bắt đầu là thời điểm hiện tại trừ 1 tháng
		LocalDateTime startDate = now.minusMonths(1);

		// Thời gian kết thúc là thời điểm hiện tại
		LocalDateTime endDate = now;

		// Chuyển LocalDateTime sang Date (với ZoneId cụ thể, ở đây là
		// ZoneId.systemDefault())
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

		// Truy vấn số lượng bài post trong khoảng thời gian này
		return postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng bài post trong 3 tháng
	@Override
	public long countPostsInThreeMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(3);
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		return postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng bài post trong 6 tháng
	@Override
	public long countPostsInSixMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(6);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng bài post trong 9 tháng
	@Override
	public long countPostsInNineMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(9);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng bài post trong 1 năm
	@Override
	public long countPostsInOneYearFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusYears(1);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng bài post từng tháng trong năm
	@Override
	public Map<String, Long> countPostsByMonthInYear() {
		LocalDateTime now = LocalDateTime.now();
		int currentYear = now.getYear();

		// Tạo một danh sách các tháng
		List<Month> months = Arrays.asList(Month.values());
		Map<String, Long> postCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

		for (Month month : months) {
			LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
			LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

			Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
			Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

			long postCount = postRepository.countPostsBetweenDates(startDateAsDate, endDateAsDate);
			postCountsByMonth.put(month.toString(), postCount);
		}

		return postCountsByMonth;
	}

	// Lấy những bài viết trong nhóm do Admin đăng
	public List<PostsResponse> findPostsByAdminRoleInGroup(int groupId, Pageable pageable) {
		List<Post> userPosts = postRepository.findPostsByAdminRoleInGroup(groupId, pageable);
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

	@Override
	public ResponseEntity<Object> findLatestPhotosByUserId(String currentUserId, String userId, Pageable pageable) {
		Optional<User> user = userService.findById(userId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("User not found.").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS);
		if (!currentUserId.equals(userId)) {
			privacyLevels = Arrays.asList(PrivacyLevel.GROUP_MEMBERS, PrivacyLevel.PRIVATE);
		}
		List<PhoToResponse> list = postRepository.findLatestPhotosByUserIdAndNotNull(privacyLevels, userId, pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved user posts successfully")
				.result(list).statusCode(HttpStatus.OK.value()).build());

	}

	@Override
	public List<PostsResponse> findPostsByAdminRoleInGroup(Integer groupId, Pageable pageable) {
		List<Post> userPosts = postRepository.findPostsByAdminRoleInGroup(groupId, pageable);
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

	@Override
	public Page<PhotosOfGroupDTO> findLatestPhotosByGroupId(Integer groupId, int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return postRepository.findPhotosOfPostByGroupId(groupId, pageable);
	}

	@Override
	public List<FilesOfGroupDTO> findLatestFilesByGroupId(Integer groupId) {
		return postRepository.findFilesOfPostByGroupId(groupId);
	}

	@Override
	public List<PostsResponse> getPostsIn1Month() {
		Date startDate = getStartOfDay(getNDaysAgo(30));
		Date endDate = getEndOfDay(new Date());
		List<Post> posts = postRepository.findByPostTimeBetween(startDate, endDate);
		return mapToPostsResponseList(posts);
	}

	// Thay đổi phương thức findAllPosts để lấy tất cả bài post của một userId
	@Override
	public Page<PostsResponse> findAllPostsByUserId(int page, int itemsPerPage, String userId) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		// Sử dụng postRepository để tìm tất cả bài post của một userId cụ thể
		Page<Post> userPostsPage = postRepository.findAllByUser_UserIdOrderByPostTimeDesc(userId, pageable);

		return userPostsPage.map(post -> {
			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			return postsResponse;
		});
	}

	@Override
	public Page<PostsResponse> findAllPostsInMonthByUserId(int page, int itemsPerPage, String userId) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Optional<User> user = userService.findById(userId);
		Date startDate = getStartOfDay(getNDaysAgo(30));
		Date endDate = getEndOfDay(new Date());
		// Sử dụng postRepository để tìm tất cả bài post của một userId cụ thể
		Page<Post> userPostsPage = postRepository.findByUserAndPostTimeBetween(user.get(), startDate, endDate,
				pageable);

		return userPostsPage.map(post -> {
			PostsResponse postsResponse = new PostsResponse(post);
			postsResponse.setComments(getIdComment(post.getComments()));
			postsResponse.setLikes(getIdLikes(post.getLikes()));
			return postsResponse;
		});
	}

	@Override
	public Map<String, Long> countPostsByUserMonthInYear(String userId) {
		LocalDateTime now = LocalDateTime.now();
		int currentYear = now.getYear();
		
		Optional<User> userOp = userService.findById(userId);
		User user = userOp.get();

		// Tạo một danh sách các tháng
		List<Month> months = Arrays.asList(Month.values());
		Map<String, Long> postCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

		for (Month month : months) {
			LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
			LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

			Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
			Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

			long postCount = postRepository.countByUserAndPostTimeBetween(user, startDateAsDate, endDateAsDate);
			postCountsByMonth.put(month.toString(), postCount);
		}

		return postCountsByMonth;
	}

}
