package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.InvitedPostGroupResponse;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Integer> {

	Optional<PostGroup> findByPostGroupName(String name);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId")
	List<GroupPostResponse> findPostGroupInfoByUserId(@Param("userId") String userId, Pageable pageable);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId  AND pgm.roleUserGroup = 'Admin'")

	List<GroupPostResponse> findPostGroupInfoByUserIdOfUser(@Param("userId") String userId);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup)start FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId  AND pgm.roleUserGroup = 'Member'")
	List<GroupPostResponse> findPostGroupInfoByUserId(@Param("userId") String userId);

	@Query("SELECT NEW vn.iostar.dto.InvitedPostGroupResponse(pg.avatarGroup, pg.backgroundGroup, pg.bio, pg.postGroupName, u.userName, u.profile.avatar) FROM PostGroupRequest pgr "
			+ "JOIN pgr.postGroup pg " + "JOIN pgr.invitingUser u " + "WHERE pgr.invitedUser.userId = :invitedUserId "
			+ "AND pgr.isAccept = false")
	List<InvitedPostGroupResponse> findPostGroupInvitedByUserId(@Param("invitedUserId") String userId);

	@Query("SELECT CASE WHEN COUNT(pg) > 0 THEN true ELSE false END FROM PostGroup pg "
			+ "JOIN pg.postGroupMembers pgm " + "JOIN pgm.user u " + "WHERE pg = :postGroup AND u = :user")
	boolean isUserInPostGroup(@Param("postGroup") PostGroup postGroup, @Param("user") User user);
}
