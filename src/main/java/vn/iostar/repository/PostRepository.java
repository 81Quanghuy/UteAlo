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

	// Lấy những bài post của cá nhân
	List<Post> findByUserUserIdOrderByPostTimeDesc(String userId);
	
	// Lấy tất cả bài post trong hệ thống
	List<Post> findAllByOrderByPostTimeDesc();

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
	
	// Lấy những bài post của nhóm
	List<Post> findByPostGroupPostGroupIdOrderByPostTimeDesc(Integer postGroupId);
	
	// Lấy tất cả các bài post của những nhóm mình tham gia
	@Query("SELECT p FROM Post p WHERE p.postGroup IN (SELECT pg FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId)")
    List<Post> findAllPostsInUserGroups(@Param("userId") String userId);
	
	@Query("SELECT p FROM Post p WHERE p.content LIKE %:searchTerm%")
    List<Post> findByContentContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

}
