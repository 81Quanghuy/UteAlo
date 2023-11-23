package vn.iostar.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.iostar.contants.RoleName;

import vn.iostar.dto.ChangePasswordRequest;
import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.PaginationInfo;
import vn.iostar.dto.UserManagerRequest;
import vn.iostar.dto.UserProfileResponse;
import vn.iostar.dto.UserResponse;
import vn.iostar.dto.UserUpdateRequest;

import vn.iostar.dto.*;

import vn.iostar.entity.PasswordResetOtp;
import vn.iostar.entity.User;
import vn.iostar.entity.VerificationToken;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.PasswordResetOtpRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.repository.VerificationTokenRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PostGroupRepository postGroupRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	VerificationTokenRepository tokenRepository;

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PasswordResetOtpRepository passwordResetOtpRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Override
	public <S extends User> S save(S entity) {
		return userRepository.save(entity);
	}

	@Override
	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Optional<User> findById(String id) {
		return userRepository.findById(id);
	}

	@Override
	public <S extends User> long count(Example<S> example) {
		return userRepository.count(example);
	}

	@Override
	public long count() {
		return userRepository.count();
	}

	@Override
	public void deleteById(String id) {
		userRepository.deleteById(id);
	}

	@Override
	public void delete(User entity) {
		userRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		userRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getProfile(String userId) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty())
			throw new RuntimeException("User not found");

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
				.result(new UserProfileResponse(user.get())).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> changePassword(String userId, ChangePasswordRequest request)
			throws Exception {

		if (request.getNewPassword().length() < 8 || request.getNewPassword().length() > 32)
			throw new RuntimeException("Password must be between 8 and 32 characters long");

		if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
			throw new RuntimeException("Password and confirm password do not match");

		Optional<User> userOptional = findById(userId);

		if (userOptional.isEmpty())
			throw new RuntimeException("User is not found");

		User user = userOptional.get();
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getAccount().getPassword()))
			throw new BadCredentialsException("Current password is incorrect");

		if (passwordEncoder.matches(request.getNewPassword(), user.getAccount().getPassword()))
			throw new RuntimeException("The new password cannot be the same as the old password");

		user.getAccount().setPassword(passwordEncoder.encode(request.getNewPassword()));
		save(user);

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Change password successful")
				.result(null).statusCode(200).build());

	}

	@Override
	public Optional<User> findByAccountEmail(String email) {
		return userRepository.findByAccountEmail(email);
	}

	@Override
	public void createPasswordResetOtpForUser(User user, String otp) {
		PasswordResetOtp myOtp = null;
		if (passwordResetOtpRepository.findByUser(user).isPresent()) {
			myOtp = passwordResetOtpRepository.findByUser(user).get();
			myOtp.updateOtp(otp);
		} else {

			myOtp = new PasswordResetOtp(otp, user);
		}
		passwordResetOtpRepository.save(myOtp);
	}

	@Override
	public String validatePasswordResetOtp(String otp) {
		Optional<PasswordResetOtp> passOtp = passwordResetOtpRepository.findByOtp(otp);
		Calendar cal = Calendar.getInstance();

		if (passOtp.isEmpty()) {
			return "Invalid token/link";
		}
		if (passOtp.get().getExpiryDate().before(cal.getTime())) {
			return "Token/link expired";
		}
		return null;
	}

	@Override
	public String validateVerificationAccount(String token) {
		VerificationToken verificationToken = tokenRepository.findByToken(token);
		if (verificationToken == null) {
			return "Invalid token, please check the token again!";
		}
		User user = verificationToken.getUser();
		user.setVerified(true);
		userRepository.save(user);
		return "Account verification successful, please login!";
	}

	@Override
	public Optional<PasswordResetOtp> getUserByPasswordResetOtp(String otp) {
		return passwordResetOtpRepository.findByOtp(otp);
	}

	@Override
	public void changeUserPassword(User user, String newPassword, String confirmPassword) {
		if (!newPassword.equals(confirmPassword))
			throw new RuntimeException("Password and confirm password do not match");
		user.getAccount().setPassword(passwordEncoder.encode(newPassword));
		save(user);
	}

	// Cập nhật thông tin người dùng
	@Override
	public ResponseEntity<Object> updateProfile(String userId, UserUpdateRequest request) throws Exception {
		Optional<User> user = findById(userId);
		if (user.isEmpty())
			throw new Exception("User doesn't exist");

		if (request.getDateOfBirth().after(new Date()))
			throw new Exception("Invalid date of birth");

		user.get().setUserName(request.getFullName());
		user.get().setPhone(request.getPhone());
		user.get().setGender(request.getGender());
		user.get().setDayOfBirth(request.getDateOfBirth());
		user.get().setAddress(request.getAddress());
		user.get().getProfile().setBio(request.getAbout());
		save(user.get());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
				.result(new UserProfileResponse(user.get())).statusCode(200).build());
	}

	// Quản lý tài khoản người dùng
	@Override
	public ResponseEntity<Object> accountManager(String authorizationHeader, UserManagerRequest request) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> userManager = findById(currentUserId);
		RoleName roleName = userManager.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<User> user = findById(request.getUserId());
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("User doesn't exist").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		user.get().getAccount().setActive(request.getIsActive());
		save(user.get());
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful")
				.result(new UserProfileResponse(user.get())).statusCode(200).build());
	}


	@Override
	public ResponseEntity<GenericResponse> getAvatarAndName(String userId) {
		Optional<User> user = findById(userId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("User doesn't exist").statusCode(HttpStatus.NOT_FOUND.value()).build());
		UserMessage userMessage = new UserMessage();
		userMessage.setAvatar(user.get().getProfile().getAvatar());
		userMessage.setUserName(user.get().getUserName());
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get avatar and name successful")
				.result(userMessage).statusCode(200).build());
	}


	// Xóa user
	@Override
	public ResponseEntity<GenericResponse> deleteUser(String idFromToken) {
		try {
			Optional<User> optionalUser = findById(idFromToken);
			/// tìm thấy user với id
			if (optionalUser.isPresent()) {
				User user = optionalUser.get();
				// không xóa user , chỉ cập nhật active về flase
				user.getAccount().setActive(false);

				User updatedUser = userRepository.save(user);
				/// nếu cập nhật active về false
				if (updatedUser != null) {
					return ResponseEntity.ok()
							.body(new GenericResponse(true, "Delete Successful!", updatedUser, HttpStatus.OK.value()));
				}
				/// cập nhật không thành công
				else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(false,
							"Update Failed!", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
				}
			}
			/// khi không tìm thấy user với id
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest()
					.body(new GenericResponse(false, "Invalid arguments!", null, HttpStatus.BAD_REQUEST.value()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse(false,
					"An internal server error occurred!", null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
		}
	}

	// Lấy toàn bộ thông tin người dùng
	@Override
	public UserProfileResponse getFullProfile(Optional<User> user, Pageable pageable) {
		UserProfileResponse profileResponse = new UserProfileResponse(user.get());
		List<FriendResponse> fResponse = friendRepository.findFriendUserIdsByUserId(user.get().getUserId());
		profileResponse.setFriends(fResponse);

		List<GroupPostResponse> groupPostResponses = postGroupRepository
				.findPostGroupInfoByUserId(user.get().getUserId(), pageable);
		profileResponse.setPostGroup(groupPostResponses);
		return profileResponse;
	}

	// Tìm tất cả người dùng trong hệ thống
	@Override
	public Page<UserResponse> findAllUsers(int page, int itemsPerPage) {
	    Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
	    Page<UserResponse> usersPage = userRepository.findAllUsers(pageable);

	    Page<UserResponse> processedUsersPage = usersPage.map(userItem -> {
	        Optional<User> userOptional = findById(userItem.getUserId());
	        if (userOptional.isPresent()) {
	        	boolean isActive = userOptional.get().getAccount().isActive();
	            // Kiểm tra isActive và thiết lập trạng thái tương ứng
	            if (isActive) {
	                userItem.setIsActive("Hoạt động");
	            } else {
	                userItem.setIsActive("Bị khóa");
	            }
	            userItem.setRoleName(userOptional.get().getRole().getRoleName());
	            userItem.setEmail(userOptional.get().getAccount().getEmail());
	        }
	        return userItem;
	    });

	    return processedUsersPage;
	}



	// Lấy tất cả người dùng trong hệ thống
	@Override
	public ResponseEntity<GenericResponseAdmin> getAllUsers(
	    String authorizationHeader, 
	    int page, 
	    int itemsPerPage
	) {
	    String token = authorizationHeader.substring(7);
	    String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
	    Optional<User> user = findById(currentUserId);
	    RoleName roleName = user.get().getRole().getRoleName();
	    if (!roleName.name().equals("Admin")) {
	        return ResponseEntity
	            .status(HttpStatus.FORBIDDEN)
	            .body(GenericResponseAdmin.builder()
	                .success(false)
	                .message("No access")
	                .statusCode(HttpStatus.FORBIDDEN.value())
	                .build());
	    }
	    

	    Page<UserResponse> users = findAllUsers(page, itemsPerPage);
	    long totalUsers = userRepository.count();
	    
	    PaginationInfo pagination = new PaginationInfo();
	    pagination.setPage(page);
	    pagination.setItemsPerPage(itemsPerPage);
	    pagination.setCount(totalUsers);
	    pagination.setPages((int) Math.ceil((double) totalUsers / itemsPerPage));

	    if (users.isEmpty()) {
	        return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(GenericResponseAdmin.builder()
	                .success(true)
	                .message("Empty")
	                .result(null)
	                .statusCode(HttpStatus.NOT_FOUND.value())
	                .build());
	    } else {
	        return ResponseEntity
	            .status(HttpStatus.OK)
	            .body(GenericResponseAdmin.builder()
	                .success(true)
	                .message("Retrieved List Users Successfully")
	                .result(users)
	                .pagination(pagination)
	                .statusCode(HttpStatus.OK.value())
	                .build());
	    }
	}

}
