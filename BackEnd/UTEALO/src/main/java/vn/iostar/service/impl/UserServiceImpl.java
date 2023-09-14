package vn.iostar.service.impl;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.iostar.dto.ChangePasswordRequest;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.UserProfileResponse;
import vn.iostar.dto.UserUpdateRequest;
import vn.iostar.entity.PasswordResetOtp;
import vn.iostar.entity.User;
import vn.iostar.entity.VerificationToken;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.PasswordResetOtpRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.repository.VerificationTokenRepository;
import vn.iostar.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	VerificationTokenRepository tokenRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	PasswordResetOtpRepository passwordResetOtpRepository;

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
	
	@Override
    public ResponseEntity<Object> updateProfile(String userId, UserUpdateRequest request) throws Exception {
        Optional<User> user = findById(userId);
        if (user.isEmpty())
            throw new Exception("User doesn't exist");

        if (request.getDateOfBirth().after(new Date()))
            throw new Exception("Invalid date of birth");

        user.get().setUserName(request.getFullName());
        user.get().setDayOfBirth(request.getDateOfBirth());
        user.get().setAddress(request.getAddress());
        user.get().getProfile().setBio(request.getAbout());
        save(user.get());

        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .message("Update successful")
                        .result(new UserProfileResponse(user.get()))
                        .statusCode(200)
                        .build()
        );
    }
	


}