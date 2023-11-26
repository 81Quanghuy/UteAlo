package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.ChangePasswordRequest;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.ListUsers;
import vn.iostar.dto.UserManagerRequest;
import vn.iostar.dto.UserProfileResponse;
import vn.iostar.dto.UserResponse;
import vn.iostar.dto.UserUpdateRequest;
import vn.iostar.entity.PasswordResetOtp;
import vn.iostar.entity.User;

public interface UserService {

	void deleteAll();

	void delete(User entity);

	void deleteById(String id);

	long count();

	<S extends User> long count(Example<S> example);

	Optional<User> findById(String id);

	Optional<User> findByAccountEmail(String email);

	void createPasswordResetOtpForUser(User user, String otp);

	List<User> findAll();

	<S extends User> S save(S entity);

	ResponseEntity<GenericResponse> getProfile(String userId);

	ResponseEntity<GenericResponse> changePassword(String userId, ChangePasswordRequest request) throws Exception;

	String validatePasswordResetOtp(String otp);

	String validateVerificationAccount(String token);

	Optional<PasswordResetOtp> getUserByPasswordResetOtp(String otp);

	void changeUserPassword(User user, String newPassword, String confirmPassword);

	ResponseEntity<Object> updateProfile(String userId, UserUpdateRequest request) throws Exception;

	ResponseEntity<GenericResponse> deleteUser(String idFromToken);

	UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable);

	// Tìm tất cả user trong hệ thống
	Page<UserResponse> findAllUsers(int page, int itemsPerPage);

	// Lấy danh sách tất cả user trong hệ thống
	ResponseEntity<GenericResponseAdmin> getAllUsers(String authorizationHeader, int page, int itemsPerPage);

	// Quản lý tài khoản của user trong hệ thống
	ResponseEntity<Object> accountManager(String authorizationHeader, UserManagerRequest request);

	ResponseEntity<GenericResponse> getAvatarAndName(String userId);
	
	List<ListUsers> getAllUsersIdAndName();

}
