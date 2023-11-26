package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.ListUsers;
import vn.iostar.dto.UserResponse;
import vn.iostar.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByPhone(String phone);

	Optional<User> findByAccountEmail(String email);
	
	// Lấy danh sách tất cả user
	@Query("SELECT NEW vn.iostar.dto.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth) FROM User u")
    Page<UserResponse> findAllUsers(Pageable pageable);
	
	@Query("SELECT NEW vn.iostar.dto.ListUsers (u.userId, u.userName) FROM User u")
    List<ListUsers> findAllUsersIdAndName();
}
