package vn.iostar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Post;

public interface PostService {

    void deleteAll();

    void delete(Post entity);

    void deleteById(Integer id);

    long count();

    List<Post> findAll();

    Optional<Post> findById(Integer id);

    <S extends Post> S save(S entity);

    ResponseEntity<GenericResponse> getPost(String userIdToken, Integer post);

    ResponseEntity<Object> updatePost(Integer postId, PostUpdateRequest request, String currentUserId) throws Exception;

    // Xóa bài post của mình
    ResponseEntity<GenericResponse> deletePost(Integer postId, String token, String userId);

    // Admin xóa bài post trong hệ thống
    ResponseEntity<GenericResponse> deletePostByAdmin(Integer postId, String authorizationHeader);

    ResponseEntity<Object> createUserPost(String token, CreatePostRequestDTO requestDTO);

    // Lấy những bài post của mình
    public List<PostsResponse> findUserPosts(String currentUserId, String userId, Pageable pageable);

    public List<PostsResponse> findUserPostsByUserIdToken(String currentUserId, Pageable pageable);

    // Tìm tất cả bài post trong hệ thống
    public Page<PostsResponse> findAllPosts(int page, int itemsPerPage);

    // Lấy tất cả bài post trong hệ thống
    ResponseEntity<GenericResponseAdmin> getAllPosts(String authorizationHeader, int page, int itemsPerPage);

    List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);

    Page<String> findLatestPhotosByUserId(String userId, int page, int size);

    List<PostsResponse> findPostGroupPosts(String userIdToken, Integer postGroupId, Pageable pageable);

    ResponseEntity<GenericResponse> getGroupPosts(String userIdToken, Integer postGroupId, Integer page, Integer size);

    List<PostsResponse> findGroupPosts(String currentUserId, Pageable pageable);

    // Lấy những bài post của 1 nhóm
    ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable);

    // Lấy những bài post trên bảng tin của người dùng
    ResponseEntity<GenericResponse> getPostTimelineByUserId(String userId, int page, int size) throws RuntimeException;

    // Thống kê bài post trong ngày hôm nay
    List<PostsResponse> getPostsToday();

    // Thống kê bài post trong 1 ngày
    List<PostsResponse> getPostsInDay(Date day);

    // Thống kê bài post trong 7 ngày
    List<PostsResponse> getPostsIn7Days();

    // Thống kê bài post trong 1 tháng
    List<PostsResponse> getPostsInMonth(Date month);

    // Đếm số lượng bài post trong ngày hôm nay
    long countPostsToday();

    // Đếm số lượng bài post trong 7 ngày
    public long countPostsInWeek();

    // Đếm số lượng bài post trong 1 tháng
    long countPostsInMonthFromNow();

    // Đếm số lượng bài post trong 1 năm
    long countPostsInOneYearFromNow();

    // Đếm số lượng bài post trong 9 tháng
    long countPostsInNineMonthsFromNow();

    // Đếm số lượng bài post trong 6 tháng
    long countPostsInSixMonthsFromNow();

    // Đếm số lượng bài post trong 3 tháng
    long countPostsInThreeMonthsFromNow();

    // Đếm số lượng bài post từng tháng trong năm
    Map<String, Long> countPostsByMonthInYear();
}
