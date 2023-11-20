package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.entity.Notification;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

}
