package vn.iostar.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.FilesOfGroupDTO;
import vn.iostar.dto.PhotosOfGroupDTO;
import vn.iostar.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	// Tìm tất cả hình ảnh từ tất cả bài post liên quan đến người dùng như bạn bè,
	// nhóm
	@Query("SELECT p FROM Post p "
			+ "WHERE ((p.user.userId = :userId OR p.user.userId IN (SELECT f.user2.userId FROM Friend f WHERE f.user1.userId = :userId) "
			+ " OR p.user.userId  IN (SELECT f.user1.userId  FROM Friend f WHERE f.user2.userId = :userId) "
			+ "OR p.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user.userId = :userId)) "
			+ "AND p.privacyLevel != 'PRIVATE' ) OR p.privacyLevel = 'ADMIN' " + "ORDER BY p.postTime DESC")
	List<Post> findPostsByUserIdAndFriendsAndGroupsOrderByPostTimeDesc(@Param("userId") String userId,
			Pageable pageable);

	// Lấy những bài post của cá nhân
	List<Post> findByUserUserIdOrderByPostTimeDesc(String userId);

	// Lấy tất cả bài post trong hệ thống
	Page<Post> findAllByOrderByPostTimeDesc(Pageable pageable);

	// Tìm tất cả hình ảnh từ tất cả bài post của một người dùng
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId ORDER BY p.postTime DESC")
	List<String> findAllPhotosByUserIdOrderByPostTimeDesc(@Param("userId") String userId);

	// Lấy 9 hình ảnh mới nhất
	@Query("SELECT p.photos FROM Post p WHERE p.user.userId = :userId AND p.privacyLevel <> 'GROUP_MEMBERS' AND p.photos IS NOT NULL AND p.photos <> '' ORDER BY p.postTime DESC")
	Page<String> findLatestPhotosByUserIdAndNotNull(String userId, Pageable pageable);

	// Lấy những bài post của nhóm
	List<Post> findByPostGroupPostGroupIdOrderByPostTimeDesc(Integer postGroupId);

	// Lấy tất cả các bài post của những nhóm mình tham gia
	@Query("SELECT p FROM Post p WHERE p.postGroup IN (SELECT pg FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId)")
	List<Post> findAllPostsInUserGroups(@Param("userId") String userId);

	@Query("SELECT p FROM Post p WHERE p.content LIKE %:searchTerm%")
	List<Post> findByContentContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

	// Lấy những bài post trong khoảng thời gian
	List<Post> findByPostTimeBetween(Date startDate, Date endDate);

	// Đếm số lượng bài post trong khoảng thời gian
	long countByPostTimeBetween(Date startDate, Date endDate);

	// Đếm số lượng bài post trong khoảng thời gian 1 tháng
	@Query("SELECT COUNT(p) FROM Post p WHERE p.postTime BETWEEN :startDate AND :endDate")
	long countPostsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

	// Lấy danh sách file của 1 nhóm
	@Query("SELECT NEW vn.iostar.dto.FilesOfGroupDTO(p.user.userId, p.user.userName, p.files, p.postGroup.postGroupId, p.postGroup.postGroupName, p.postId) "
			+ "FROM Post p " + "WHERE p.postGroup.postGroupId = :groupId AND p.files IS NOT NULL")
	Page<FilesOfGroupDTO> findFilesOfPostByGroupId(int groupId, Pageable pageable);

	// Lấy danh sách photo của 1 nhóm
	@Query("SELECT NEW vn.iostar.dto.PhotosOfGroupDTO(p.user.userId, p.user.userName, p.photos, p.postGroup.postGroupId, p.postGroup.postGroupName, p.postId) "
			+ "FROM Post p " + "WHERE p.postGroup.postGroupId = :groupId AND p.photos IS NOT NULL")
	Page<PhotosOfGroupDTO> findPhotosOfPostByGroupId(int groupId, Pageable pageable);

	// Lấy những bài viết trong nhóm do Admin đăng
//	@Query("SELECT p, pgm.roleUserGroup " + "FROM Post p " + "JOIN p.postGroup pg " + "JOIN pg.postGroupMembers pgm "
//			+ "WHERE pgm.roleUserGroup = vn.iostar.contants.RoleUserGroup.Admin " + "AND pg.postGroupId = :groupId "
//			+ "AND p.user.userId = pgm.user.userId") // So sánh userId trong Post với userId trong PostGroupMember
	@Query("SELECT p " + "FROM Post p " + "JOIN p.postGroup pg " + "JOIN pg.postGroupMembers pgm "
	+ "WHERE pgm.roleUserGroup = vn.iostar.contants.RoleUserGroup.Admin " + "AND pg.postGroupId = :groupId "
	+ "AND p.user.userId = pgm.user.userId") // So sánh userId trong Post với userId trong PostGroupMember
	List<Post> findPostsByAdminRoleInGroup(int groupId);

}
