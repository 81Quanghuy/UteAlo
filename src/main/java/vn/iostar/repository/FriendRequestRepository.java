package vn.iostar.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.entity.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

	@Query("SELECT NEW vn.iostar.dto.FriendRequestResponse(u.userId, u.profile.avatar, u.userName)  FROM FriendRequest fr JOIN User u ON fr.userFrom.userId = u.userId WHERE fr.userTo.userId = :userId")
	List<FriendRequestResponse> findUserFromUserIdByUserToUserIdPageable(@Param("userId") String userId, Pageable pageable);

	/*
	 * @Query("SELECT NEW vn.iostar.dto.FriendRequestResponse(u.userId, u.profile.avatar, u.userName)  FROM FriendRequest fr JOIN User u ON fr.userFrom.userId = u.userId WHERE fr.userTo.userId = :userId"
	 * ) List<FriendRequestResponse> findUserFromUserIdByUserToUserId(String
	 * userId);
	 */

	List<FriendRequest> findByUserFromUserIdAndUserToUserId(String userFromId, String userToID);
	
}
