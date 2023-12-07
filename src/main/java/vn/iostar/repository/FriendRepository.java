package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.FriendResponse;
import vn.iostar.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Integer> {

	// Get List userId by UserID where become friend
	@Query("SELECT DISTINCT NEW vn.iostar.dto.FriendResponse(u.userId,u.profile.background, u.profile.avatar, u.userName,u.isOnline) FROM Friend f "
			+ "JOIN f.user1 u1 " + "JOIN f.user2 u2 " + "JOIN User u ON u = u1 OR u = u2 "
			+ "WHERE (u1.userId = :userId OR u2.userId = :userId) " + "AND u.userId <> :userId")
	List<FriendResponse> findFriendUserIdsByUserId(@Param("userId") String userId);

	// Get List userId by UserID where become friend
	@Query("SELECT DISTINCT NEW vn.iostar.dto.FriendResponse(u.userId,u.profile.background, u.profile.avatar, u.userName,u.isOnline) FROM Friend f "
			+ "JOIN f.user1 u1 " + "JOIN f.user2 u2 " + "JOIN User u ON u = u1 OR u = u2 "
			+ "WHERE (u1.userId = :userId OR u2.userId = :userId) " + "AND u.userId <> :userId")
	List<FriendResponse> findFriendByUserId(@Param("userId") String userId, Pageable pageable);

	Optional<Friend> findByUser1UserIdAndUser2UserId(String userId, String userId2);
}
