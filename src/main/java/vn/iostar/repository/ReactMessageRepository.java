package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.ReactMessage;

@Repository
public interface ReactMessageRepository extends JpaRepository<ReactMessage, String> {

    Optional<ReactMessage> findReactMessageByMessageMessageIdAndUserUserId(String messageId, String userId);
}
