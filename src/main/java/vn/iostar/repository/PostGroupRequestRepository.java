package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.PostGroupRequest;

@Repository
public interface PostGroupRequestRepository extends JpaRepository<PostGroupRequest, String> {

	Optional<PostGroupRequest> findByInvitedUserUserIdAndPostGroupPostGroupId( String invitedUserId, Integer postGroupId);
}
