package vn.iostar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import vn.iostar.entity.Post;
import vn.iostar.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByUserUserId(String userId);

	@Query("SELECT p FROM Post p " + "WHERE p.user = :user " + // Bài viết của chính user đó
			"OR p.user IN (SELECT f.user2 FROM Friend f WHERE f.user1 = :user) " + // Bài viết của bạn bè của user
			"OR p.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user = :user) "
			+ "ORDER BY p.postTime DESC") // Sắp xếp theo thời gian tạo giảm dần
	List<Post> findPostsByUserAndFriendsAndGroupsOrderByPostTimeDesc(@Param("user") User user);

}
