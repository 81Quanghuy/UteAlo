package vn.iostar.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.contants.PrivacyLevel;
import vn.iostar.entity.Post;
import vn.iostar.entity.Share;

@Repository
public interface ShareRepository extends JpaRepository<Share, Integer> {

    List<Share> findByUserUserId(String userId, Pageable pageable);

    // Lấy các bài post của user theo userId có privacyLevel là PUBLIC Or FRIENDS OrderBy PostTime Desc
    List<Share> findByUserUserIdAndPrivacyLevelInOrderByCreateAtDesc(String userId, Collection<PrivacyLevel> privacyLevel, Pageable pageable);

    @Query("SELECT s FROM Share s "
            + "WHERE ((s.user.userId = :userId OR (s.user.userId IN (SELECT f.user2.userId FROM Friend f WHERE f.user1.userId = :userId)AND s.privacyLevel!='GROUP_MEMBERS') "
            + "OR ( s.user.userId IN (SELECT f.user1.userId FROM Friend f WHERE f.user2.userId = :userId)AND s.privacyLevel!='GROUP_MEMBERS') "
            + "OR s.postGroup IN (SELECT pgm.postGroup FROM PostGroupMember pgm WHERE pgm.user.userId = :userId)) "
            + "AND s.privacyLevel != 'PRIVATE' ) OR s.privacyLevel = 'ADMIN' " + "ORDER BY s.createAt DESC")
    List<Share> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(@Param("userId") String userId, Pageable pageable);

    void deleteByPostPostId(int postId);

    List<Share> findByPostGroupPostGroupId(Integer postGroupId, Pageable pageable);

    @Query("SELECT p FROM Share p WHERE p.postGroup IN (SELECT pg FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId)")
    List<Share> findAllSharesInUserGroups(@Param("userId") String userId, Pageable pageable);
}
