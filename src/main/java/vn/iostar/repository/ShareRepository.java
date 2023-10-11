package vn.iostar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Share;
import vn.iostar.entity.User;

@Repository
public interface ShareRepository extends JpaRepository<Share, Integer> {

	List<Share> findByUserUserId(String userId);

	@Query("SELECT s FROM Share s " + "WHERE s.user = :user "
			+ "OR s.user IN (SELECT f.user2 FROM Friend f WHERE f.user1 = :user) "
			+ "OR s.post.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user = :user) "
			+ "ORDER BY s.post.postTime DESC")
	List<Share> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(@Param("user") User user);

}
