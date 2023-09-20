package vn.iostar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

	@Query("SELECT fr.userFrom.userId FROM FriendRequest fr WHERE fr.userTo.userId = :userId")
	List<String> findUserFromUserIdByUserToUserId(String userId);

	List<FriendRequest> findByUserFromUserIdAndUserToUserId(String userFromId, String userToID);
	
}
