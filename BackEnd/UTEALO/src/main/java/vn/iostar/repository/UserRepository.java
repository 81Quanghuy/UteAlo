package vn.iostar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import vn.iostar.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByPhone(String phone);
}
