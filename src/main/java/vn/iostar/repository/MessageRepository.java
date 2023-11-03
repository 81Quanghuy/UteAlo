package vn.iostar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Message;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

	@Query("SELECT m FROM Message m WHERE (m.senderId = :senderId AND m.receiverId = :receiverId) OR (m.senderId = :receiverId AND m.receiverId = :senderId) ORDER BY m.createAt")
	List<Message> findMessagesBetweenUsers(String senderId, String receiverId);

	List<Message> findByGroupIdOrderByCreateAt(String groupId);
}
