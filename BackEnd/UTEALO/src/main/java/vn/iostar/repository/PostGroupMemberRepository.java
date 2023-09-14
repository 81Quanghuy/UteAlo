package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.PostGroupMember;

@Repository
public interface PostGroupMemberRepository extends JpaRepository<PostGroupMember, Integer> {

}
