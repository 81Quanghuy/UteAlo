package vn.iostar.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

import vn.iostar.contants.RoleName;
import vn.iostar.dto.*;
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
    public ResponseEntity<GenericResponse> getPost(Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty())
            throw new RuntimeException("Post not found");

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
                .result(new PostResponse(post.get())).statusCode(HttpStatus.OK.value()).build());
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
        if (postOp.isEmpty())
            throw new Exception("Post doesn't exist");
        Post post = postOp.get();
        if (!currentUserId.equals(postOp.get().getUser().getUserId()))
            throw new Exception("Update denied");
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());
        post.setPrivacyLevel(request.getPrivacyLevel());
        post.setUpdateAt(new Date());
        try {
            if (request.getPhotos() == null || request.getPhotos().getContentType() == null) {
                post.setPhotos("");
            } else if (request.getPhotos().equals(postOp.get().getPhotos())) {
                post.setPhotos(postOp.get().getPhotos());
            } else {
                post.setPhotos(cloudinaryService.uploadImage(request.getPhotos()));
            }

            if (request.getFiles() == null || request.getFiles().getContentType() == null) {
                post.setFiles("");
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
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
                .statusCode(200).build());
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

    @Override
    public ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO) {

        List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

        if (String.valueOf(requestDTO.getPostGroupId()) == null) {
            return ResponseEntity.badRequest().body("Please select post group");
        }
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

    // Lấy những bài post của cá nhân
    public List<PostsResponse> findUserPosts(String userId) {
        List<Post> userPosts = postRepository.findByUserUserIdOrderByPostTimeDesc(userId);
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

    // Lấy tất cả bài post trong hệ thống
    @Override
    public Page<PostsResponse> findAllPosts(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Post> userPostsPage = postRepository.findAll(pageable);

        Page<PostsResponse> simplifiedUserPostsPage = userPostsPage.map(post -> {
            PostsResponse postsResponse = new PostsResponse(post);
            postsResponse.setComments(getIdComment(post.getComments()));
            postsResponse.setLikes(getIdLikes(post.getLikes()));
            return postsResponse;
        });

        return simplifiedUserPostsPage;
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
            return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                    .message("Retrieved List Posts Successfully").result(userPostsPage)
                    .pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy những bài post của nhóm
    @Override
    public List<PostsResponse> findPostGroupPosts(Integer postGroupId) {
        List<Post> groupPosts = postRepository.findByPostGroupPostGroupIdOrderByPostTimeDesc(postGroupId);
        List<PostsResponse> simplifiedGroupPosts = new ArrayList<>();
        for (Post post : groupPosts) {
            PostsResponse postsResponse = new PostsResponse(post);
            postsResponse.setComments(getIdComment(post.getComments()));
            postsResponse.setLikes(getIdLikes(post.getLikes()));
            simplifiedGroupPosts.add(postsResponse);
        }
        return simplifiedGroupPosts;
    }

    // Lấy những bài post của nhóm
    @Override
    public ResponseEntity<GenericResponse> getGroupPosts(Integer postGroupId) {
        List<PostsResponse> groupPosts = findPostGroupPosts(postGroupId);
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
    public List<PostsResponse> findGroupPosts(String currentUserId) {
        List<Post> groupPosts = postRepository.findAllPostsInUserGroups(currentUserId);
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
    public ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, String userId) {
        List<PostsResponse> groupPosts = findGroupPosts(currentUserId);
        if (currentUserId.isEmpty()) {
            throw new RuntimeException("User not found.");
        } else if (groupPosts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("No posts found for this group").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity
                    .ok(GenericResponse.builder().success(true).message("Retrieved group posts successfully")
                            .result(groupPosts).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> getPostTimelineByUserId(String userId, int page, int size) throws RuntimeException {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("User not found.").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        PageRequest pageable = PageRequest.of(page, size);
        List<Post> listPost = postRepository.findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(userId,
                pageable);
        // Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
//		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
        List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
        for (Post post : listPost) {
            PostsResponse postsResponse = new PostsResponse(post);
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

    // Lấy những bài post liên quan đến user như cá nhân, nhóm, bạn bè
//	@Override
//	public List<PostsResponse> findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(String userId) {
//		List<Post> userPosts = postRepository.findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(userId);
//		// Loại bỏ các thông tin không cần thiết ở đây, chẳng hạn như user và role.
//		// Có thể tạo một danh sách mới chứa chỉ các thông tin cần thiết.
//		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
//		for (Post post : userPosts) {
//			PostsResponse postsResponse = new PostsResponse(post);
//			if (post.getComments() != null && !post.getComments().isEmpty()) {
//				postsResponse.setComments(getIdComment(post.getComments()));
//			} else {
//				postsResponse.setComments(new ArrayList<>());
//			}
//			if (post.getLikes() != null && !post.getLikes().isEmpty()) {
//				postsResponse.setLikes(getIdLikes(post.getLikes()));
//			} else {
//				postsResponse.setLikes(new ArrayList<>());
//			}
//			simplifiedUserPosts.add(postsResponse);
//		}
//		return simplifiedUserPosts;
//	}

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
    public PostsResponse getPost(Post post) {
        PostsResponse postsResponse = new PostsResponse(post);
        postsResponse.setComments(getIdComment(post.getComments()));
        postsResponse.setLikes(getIdLikes(post.getLikes()));
        return postsResponse;
    }

    @Override
    public List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId) {
        return postRepository.findAllPhotosByUserIdOrderByPostTimeDesc(userId);
    }

    @Override
    public Page<String> findLatestPhotosByUserId(String userId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return postRepository.findLatestPhotosByUserIdAndNotNull(userId, pageable);
    }

}
