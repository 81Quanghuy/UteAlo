package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.GroupPostResponse;
import vn.iostar.entity.PostGroup;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Integer> {
	Optional<PostGroup> findByPostGroupName(String postGroupName);
	
	@Query("SELECT NEW vn.iostar.dto.GroupPostResponse(pg.postGroupId, pg.postGroupName, pgm.roleUserGroup) FROM PostGroup pg JOIN pg.postGroupMembers pgm WHERE pgm.user.userId = :userId")
    List<GroupPostResponse> findPostGroupInfoByUserId(@Param("userId") String userId, Pageable pageable);
}
