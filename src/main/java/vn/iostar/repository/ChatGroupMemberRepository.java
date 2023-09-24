package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.ChatGroupMember;

@Repository
public interface ChatGroupMemberRepository extends JpaRepository<ChatGroupMember, Integer> {

}
