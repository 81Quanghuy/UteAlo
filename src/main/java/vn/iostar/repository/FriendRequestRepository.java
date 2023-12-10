package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.FriendResponse;
import vn.iostar.entity.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

	@Query("SELECT NEW vn.iostar.dto.FriendResponse(u.userId,u.profile.background, u.profile.avatar, u.userName,u.isOnline)  FROM FriendRequest fr JOIN User u ON fr.userFrom.userId = u.userId WHERE fr.userTo.userId = :userId")
	List<FriendResponse> findUserFromUserIdByUserToUserId(@Param("userId") String userId);

	@Query("SELECT NEW vn.iostar.dto.FriendResponse(u.userId,u.profile.background, u.profile.avatar, u.userName,u.isOnline)   FROM FriendRequest fr JOIN User u ON fr.userFrom.userId = u.userId WHERE fr.userTo.userId = :userId")
	List<FriendResponse> findUserFromUserIdByUserToUserIdPageable(@Param("userId") String userId, Pageable pageable);

	@Query("SELECT NEW vn.iostar.dto.FriendResponse(u.userId,u.profile.background, u.profile.avatar, u.userName,u.isOnline)  FROM FriendRequest fr JOIN User u ON fr.userTo.userId = u.userId WHERE fr.userFrom.userId = :userId")
	List<FriendResponse> findUserToUserIdByUserFromUserIdPageable(@Param("userId") String userId);

	@Query("SELECT DISTINCT NEW vn.iostar.dto.FriendResponse(u.userId, u.profile.background, u.profile.avatar, u.userName,u.isOnline) FROM User u "
			+ "JOIN u.postGroupMembers pgm " + "JOIN pgm.postGroup pg " + "JOIN pg.postGroupMembers sharedPgm "
			+ "WHERE sharedPgm.user.userId = :userId " + "AND u.userId != :userId " + "AND ("
			+ "   (SELECT COUNT(f.friendId) " + "    FROM Friend f "
			+ "    WHERE (f.user1.userId = u.userId AND f.user2.userId = :userId ) "
			+ "       OR (f.user1.userId = :userId AND f.user2.userId = u.userId)" + "   ) = 0) " + "AND ("
			+ "   (SELECT COUNT(fr.friendRequestId) " + "    FROM FriendRequest fr "
			+ "    WHERE fr.userTo.userId = u.userId AND fr.userFrom.userId = :userId" + "   ) = 0) " + "AND ("
			+ "   (SELECT COUNT(fr.friendRequestId) " + "    FROM FriendRequest fr "
			+ "    WHERE fr.userTo.userId = :userId AND fr.userFrom.userId = u.userId" + "   ) = 0)")
	List<FriendResponse> findSuggestionListByUserId(@Param("userId") String userId);

	Optional<FriendRequest> findByUserFromUserIdAndUserToUserId(String userFromId, String userToID);
}
