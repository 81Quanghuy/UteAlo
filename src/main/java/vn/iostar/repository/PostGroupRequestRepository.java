package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.PostGroupRequest;

@Repository
public interface PostGroupRequestRepository extends JpaRepository<PostGroupRequest, String> {

	Optional<PostGroupRequest> findByInvitedUserUserIdAndPostGroupPostGroupId(String invitedUserId,
			Integer postGroupId);

	List<PostGroupRequest> findByIsAcceptAndPostGroupPostGroupId(Boolean isAccept, Integer postGroupId);

	@Query("SELECT pgr FROM PostGroupRequest pgr " + "WHERE pgr.invitingUser.userId = :invitingUserId "
			+ "AND pgr.postGroupRequestId = :postGroupRequestId")
	Optional<PostGroupRequest> findInvitationSentByUserIdAndRequestId(@Param("invitingUserId") String userId,
			@Param("postGroupRequestId") String postGroupRequestId);

	@Query("SELECT pgr FROM PostGroupRequest pgr "
			+ "WHERE pgr.invitingUser.userId = :invitingUserId AND pgr.invitedUser.userId = :invitedUserId "
			+ "AND pgr.postGroup.postGroupId = :postGroupId")
	Optional<PostGroupRequest> findRequestJoinGroup(@Param("invitingUserId") String invitingUserId,
			@Param("invitedUserId") String invitedUserId, @Param("postGroupId") Integer postGroupId);
}
