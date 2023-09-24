package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.ChatGroup;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Integer> {

	Optional<ChatGroup> findByGroupName(String groupName);
}
