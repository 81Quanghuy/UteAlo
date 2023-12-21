package vn.iostar.service;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.contants.RoleName;
import vn.iostar.dto.*;
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

	// Thống kê user trong ngày hôm nay
	List<UserResponse> getUsersToday();

	// Thống kê user trong 1 ngày
	List<UserResponse> getUsersInDay(Date day);

	// Thống kê user trong 7 ngày
	List<UserResponse> getUsersIn7Days();

	// Thống kê user trong 1 tháng
	List<UserResponse> getUsersInMonth(Date month);

	// Đếm số lượng user từng tháng trong năm
	Map<String, Long> countUsersByMonthInYear();

	// Đếm số lượng user trong ngày hôm nay
	long countUsersToday();

	// Đếm số lượng user trong 7 ngày
	public long countUsersInWeek();

	// Đếm số lượng user trong 1 tháng
	long countUsersInMonthFromNow();

	// Đếm số lượng user trong 1 năm
	long countUsersInOneYearFromNow();

	// Đếm số lượng user trong 9 tháng
	long countUsersInNineMonthsFromNow();

	// Đếm số lượng user trong 6 tháng
	long countUsersInSixMonthsFromNow();

	// Đếm số lượng user trong 3 tháng
	long countUsersInThreeMonthsFromNow();

	void changeOnlineStatus(UserDTO user);

	// Thống kê user trong 1 tháng
	List<UserResponse> getUsersIn1Month();
	
	// Tính % người dùng mới trong tháng
	double calculatePercentageNewUsersThisMonth();
	
	// Top 3 người dùng đóng góp nhiều nhất trong tháng
	List<Top3UserOfMonth> getTop3UsersWithMostActivityInMonth();

	ResponseEntity<Object> createAccount(String authorizationHeader, AccountManager request) throws IOException, ParseException;

	ResponseEntity<Object> searchUser(String fields, String query);
	UserStatisticsDTO getUserStatistics(String userId);

	List<User> findByRoleRoleName(RoleName roleName);

}
