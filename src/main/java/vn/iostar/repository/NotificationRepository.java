package vn.iostar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.entity.Notification;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

	List<Notification> findByUserUserIdOrderByCreateAtDesc(String userIdToken, Pageable pageable);

	List<Notification> findByUserUserIdAndIsReadOrderByCreateAtDesc(String userIdToken, Boolean isRead,
			Pageable pageable);

	List<Notification> findByUserUserId(String userIdToken);

	@Query("Select isRead from Notification n Where n.notificationId = :notifyId ")
	Optional<Boolean> findByNotificationId(@Param("notifyId") String notifyId);

	@Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.notificationId = :notifyId")
    int markAsRead(@Param("notifyId") String notifyId);
}
