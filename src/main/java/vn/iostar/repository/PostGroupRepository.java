package vn.iostar.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.InvitedPostGroupResponse;
import vn.iostar.dto.SearchPostGroup;
import vn.iostar.dto.SearchUser;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Integer> {

	Optional<PostGroup> findByPostGroupName(String name);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId")
	List<GroupPostResponse> findPostGroupInfoByUserId(@Param("userId") String userId, Pageable pageable);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId  AND (pgm.roleUserGroup = 'Admin' OR pgm.roleUserGroup = 'Deputy')")

	List<GroupPostResponse> findPostGroupInfoByUserIdOfUser(@Param("userId") String userId);

	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId  AND pgm.roleUserGroup = 'Member'")
	List<GroupPostResponse> findPostGroupInfoByUserId(@Param("userId") String userId);

	// Lời mời vào nhóm đã nhận được
	@Query("SELECT NEW vn.iostar.dto.InvitedPostGroupResponse(pgr.postGroupRequestId, pg.postGroupId, pg.avatarGroup, pg.backgroundGroup, pg.bio, pg.postGroupName, u.userName, u.profile.avatar, u.userId) FROM PostGroupRequest pgr "
			+ "JOIN pgr.postGroup pg " + "JOIN pgr.invitingUser u " + "WHERE pgr.invitedUser.userId = :invitedUserId "
			+ "AND pgr.isAccept = false")
	List<InvitedPostGroupResponse> findPostGroupInvitedByUserId(@Param("invitedUserId") String userId);

	@Query("SELECT CASE WHEN COUNT(pg) > 0 THEN true ELSE false END FROM PostGroup pg "
			+ "JOIN pg.postGroupMembers pgm " + "JOIN pgm.user u " + "WHERE pg = :postGroup AND u = :user")
	boolean isUserInPostGroup(@Param("postGroup") PostGroup postGroup, @Param("user") User user);

	// Lời mời vào nhóm đã gửi đi
	@Query("SELECT NEW vn.iostar.dto.InvitedPostGroupResponse(pgr.postGroupRequestId, pg.postGroupId, pg.avatarGroup, pg.backgroundGroup, pg.bio, pg.postGroupName, u.userName, u.profile.avatar, u.userId) FROM PostGroupRequest pgr "
			+ "JOIN pgr.postGroup pg " + "JOIN pgr.invitedUser u " + "WHERE pgr.invitingUser.userId = :invitingUserId "
			+ "AND pgr.isAccept = false")
	List<InvitedPostGroupResponse> findPostGroupRequestsSentByUserId(@Param("invitingUserId") String userId);

	@Query("SELECT  NEW vn.iostar.dto.SearchPostGroup (pg.postGroupId,pg.postGroupName,pg.avatarGroup,pg.bio,pg.isPublic) FROM PostGroup pg WHERE pg.postGroupName LIKE %:search%")
	List<SearchPostGroup> findPostGroupNamesContainingIgnoreCase(@Param("search") String search);

	@Query("SELECT  NEW vn.iostar.dto.SearchPostGroup (pg.postGroupId,pg.postGroupName,pg.avatarGroup,pg.bio,pg.isPublic) FROM PostGroup pg WHERE pg.postGroupName LIKE %:search%")
	List<SearchPostGroup> findTop3PostGroupNamesContainingIgnoreCase(@Param("search") String search, Pageable pageable);

	@Query("SELECT NEW vn.iostar.dto.SearchUser(u.userId, u.userName) " + "FROM User u "
			+ "WHERE u.userName LIKE %:search%")
	List<SearchUser> findUsersByName(@Param("search") String search);

	@Query("SELECT NEW vn.iostar.dto.SearchUser(u.userId, u.userName) " + "FROM User u "
			+ "WHERE u.userName LIKE %:search%")
	List<SearchUser> findTop3UsersByName(@Param("search") String search, Pageable pageable);

	@Query("SELECT NEW vn.iostar.dto.SearchPostGroup(pg.postGroupId, pg.postGroupName, pg.avatarGroup, pg.bio, pg.isPublic) FROM PostGroup pg ORDER BY pg.createDate DESC")
	Page<SearchPostGroup> findAllPostGroups(Pageable pageable);

	// Đếm số lượng nhóm trong khoảng thời gian
	long countByCreateDateBetween(Date startDateAsDate, Date endDateAsDate);
	
	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName,pg.avatarGroup, pg.backgroundGroup, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId ")
	Page<GroupPostResponse> findPostGroupByUserId(@Param("userId") String userId, Pageable pageable);
	
	@Query("SELECT NEW vn.iostar.dto.SearchPostGroup(pg.postGroupId, pg.postGroupName, pg.avatarGroup, pg.bio, pg.isPublic) FROM PostGroup pg WHERE pg.createDate BETWEEN :startDate AND :endDate")
	List<SearchPostGroup> findPostGroupByCreateDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
