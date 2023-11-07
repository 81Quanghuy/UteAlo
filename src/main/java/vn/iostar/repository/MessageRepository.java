package vn.iostar.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Message;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

	@Query("SELECT m FROM Message m WHERE (m.senderId = :senderId AND m.receiverId = :receiverId) OR (m.senderId = :receiverId AND m.receiverId = :senderId) ORDER BY m.createAt DESC")
	List<Message> findMessagesBetweenUsers(String senderId, String receiverId, PageRequest pageable);

	List<Message> findByGroupIdOrderByCreateAt(String groupId, PageRequest pageable);

	Optional<Message> findByCreateAtAndSenderIdAndReceiverIdAndContent(Date date, String senderId, String receiverId,
			String content);
}
