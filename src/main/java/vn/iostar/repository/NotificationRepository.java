package vn.iostar.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.Notification;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByUserUserIdOrderByCreateAtDesc(String userIdToken, Pageable pageable);

    List<Notification> findByUserUserIdAndIsReadOrderByCreateAtDesc(String userIdToken, Boolean isRead, Pageable pageable);

    List<Notification> findByUserUserId(String userIdToken);
}
