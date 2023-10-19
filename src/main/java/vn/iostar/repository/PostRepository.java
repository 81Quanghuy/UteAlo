package vn.iostar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Post;
import vn.iostar.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	List<Post> findByUserUserIdOrderByPostTimeDesc(String userId);

	// Tìm tất cả hình ảnh từ tất cả bài post liên quan đến người dùng như bạn bè, nhóm 
	@Query("SELECT p FROM Post p "
			+ "WHERE (p.user = :user OR p.user IN (SELECT f.user2 FROM Friend f WHERE f.user1 = :user) "
			+ "OR p.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user = :user)) "
			+ "AND p.privacyLevel != 'PRIVATE' " + "ORDER BY p.postTime DESC")
	List<Post> findPostsByUserAndFriendsAndGroupsOrderByPostTimeDesc(@Param("user") User user);

	// Tìm tất cả hình ảnh từ tất cả bài post của một người dùng
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId ORDER BY p.postTime DESC")
	List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@Param("userId") String userId);

	// Lấy 9 hình ảnh mới nhất
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId AND p.photos IS NOT NULL AND p.photos <> '' ORDER BY p.postTime DESC")
	Page<String> findLatestPhotosByUserIdAndNotNull(String userId, Pageable pageable);

}
