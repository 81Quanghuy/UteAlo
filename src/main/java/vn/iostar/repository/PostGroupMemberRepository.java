package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.contants.RoleUserGroup;
import vn.iostar.entity.PostGroupMember;

@Repository
public interface PostGroupMemberRepository extends JpaRepository<PostGroupMember, Integer> {

	Optional<PostGroupMember> findByUserUserIdAndRoleUserGroup(String id, RoleUserGroup roleUserGroup);

	// Tìm kiếm dữ liệu trong bảng post_group_post_group_member
	@Query(value = "SELECT CASE WHEN EXISTS (SELECT 1 FROM post_group_post_group_member pgm JOIN PostGroupMember pg ON pgm.post_group_member_id = pg.post_group_member_id WHERE pgm.post_group_id = ?1 AND pg.user_id = ?2) THEN 1 ELSE 0 END", nativeQuery = true)
	int hasPostGroupMemberAssociations(int postGroupId, String userId);

	// Xóa dữ liệu trong bảng post_group_post_group_member
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM post_group_post_group_member WHERE post_group_id = ?1 AND post_group_member_id IN (SELECT post_group_member_id FROM PostGroupMember WHERE user_id = ?2)", nativeQuery = true)
	void deletePostGroupMemberAssociations(int postGroupId, String userId);

	@Query("SELECT COUNT(pgm) FROM PostGroupMember pgm JOIN pgm.postGroup pg WHERE pgm.user.userId = :userId AND pg.postGroupId = :postGroupId")
	int countFriendsInGroup(@Param("userId") String userId, @Param("postGroupId") int postGroupId);
	

}
