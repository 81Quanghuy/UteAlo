package vn.iostar.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    // Tìm tất cả hình ảnh từ tất cả bài post liên quan đến người dùng như bạn bè, nhóm
    @Query("SELECT p FROM Post p "
            + "WHERE ((p.user.userId = :userId OR p.user.userId IN (SELECT f.user2.userId FROM Friend f WHERE f.user1.userId = :userId) "
            + " OR p.user.userId  IN (SELECT f.user1.userId  FROM Friend f WHERE f.user2.userId = :userId) "
            + "OR p.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user.userId = :userId)) "
            + "AND p.privacyLevel != 'PRIVATE' ) OR p.privacyLevel = 'ADMIN' " + "ORDER BY p.postTime DESC")
    List<Post> findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(@Param("userId") String userId, Pageable pageable);

	// Lấy những bài post của cá nhân
	List<Post> findByUserUserIdOrderByPostTimeDesc(String userId);

	// Lấy tất cả bài post trong hệ thống
	Page<Post> findAllByOrderByPostTimeDesc(Pageable pageable);

	// Tìm tất cả hình ảnh từ tất cả bài post của một người dùng
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId ORDER BY p.postTime DESC")
	List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@Param("userId") String userId);

	// Lấy 9 hình ảnh mới nhất
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId AND p.photos IS NOT NULL AND p.photos <> '' ORDER BY p.postTime DESC")
	Page<String> findLatestPhotosByUserIdAndNotNull(String userId, Pageable pageable);

	// Lấy những bài post của nhóm
	List<Post> findByPostGroupPostGroupIdOrderByPostTimeDesc(Integer postGroupId);

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@Query("SELECT p FROM Post p WHERE p.postGroup IN (SELECT pg FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId)")
	List<Post> findAllPostsInUserGroups(@Param("userId") String userId);

	@Query("SELECT p FROM Post p WHERE p.content LIKE %:searchTerm%")
	List<Post> findByContentContaining(@Param("searchTerm") String searchTerm, Pageable pageable);
	
	// Lấy những bài post trong khoảng thời gian
	List<Post> findByPostTimeBetween(Date startDate, Date endDate);
	
	// Đếm số lượng bài post trong khoảng thời gian
	long countByPostTimeBetween(Date startDate, Date endDate);
	
	// Đếm số lượng bài post trong khoảng thời gian 1 tháng
	@Query("SELECT COUNT(p) FROM Post p WHERE p.postTime BETWEEN :startDate AND :endDate")
    long countPostsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
