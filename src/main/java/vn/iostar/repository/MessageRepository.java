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

	@Query("SELECT m FROM Message m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId) ORDER BY m.createAt DESC")
	List<Message> findMessagesBetweenUsers(String senderId, String receiverId, PageRequest pageable);

	List<Message> findByGroupPostGroupIdOrderByCreateAt(int postGroupId,PageRequest pageable);

	Optional<Message> findByCreateAtAndSenderUserIdAndReceiverUserIdAndContent(Date date, String senderId, String receiverId,
			String content);
}
