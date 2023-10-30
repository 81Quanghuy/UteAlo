package vn.iostar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

}
