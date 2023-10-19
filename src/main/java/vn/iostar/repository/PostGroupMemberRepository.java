package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.contants.RoleUserGroup;
import vn.iostar.entity.PostGroupMember;

@Repository
public interface PostGroupMemberRepository extends JpaRepository<PostGroupMember, Integer> {

	Optional<PostGroupMember> findByUserUserIdAndRoleUserGroup(String id,RoleUserGroup roleUserGroup );
}
