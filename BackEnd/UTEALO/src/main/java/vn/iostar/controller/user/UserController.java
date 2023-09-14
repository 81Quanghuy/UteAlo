package vn.iostar.controller.user;

import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.AccessDeniedException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import vn.iostar.dto.ChangePasswordRequest;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PasswordResetRequest;
import vn.iostar.dto.UserProfileResponse;
import vn.iostar.dto.UserUpdateRequest;
import vn.iostar.entity.User;
import vn.iostar.exception.UserNotFoundException;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.AccountService;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	JavaMailSender javaMailSender;

	@Autowired
	UserService userService;

	@Autowired
	AccountService accountService;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	Environment env;

	@GetMapping("/home")
	public String homePage() {
		return "Hello User";
	}

	@GetMapping("/profile")
	public ResponseEntity<GenericResponse> getInformation(@RequestHeader("Authorization") String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		return userService.getProfile(userId);
	}

	@GetMapping("/profile/{userId}")
	public ResponseEntity<GenericResponse> getInformation(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("userId") String userId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(userId);
		if (user.isEmpty()) {
			throw new RuntimeException("User not found.");
		} else if (currentUserId.equals(userId)) {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieving user profile successfully and access update")
					.result(new UserProfileResponse(user.get())).statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieving user profile successfully and access update denied")
					.result(new UserProfileResponse(user.get())).statusCode(HttpStatus.OK.value()).build());
		}
	}

	@PutMapping("/change-password")
	public ResponseEntity<GenericResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request,
			@RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {
		String token = authorizationHeader.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(token);
		if (bindingResult.hasErrors()) {
			throw new RuntimeException(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
		}

		return userService.changePassword(userId, request);
	}

	@PostMapping("/forgot-password")
	public GenericResponse resetPassword(@RequestParam final String email)
			throws MessagingException, UnsupportedEncodingException {
		User user = userService.findByAccountEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User Not Found"));

		String otp = UUID.randomUUID().toString();
		userService.createPasswordResetOtpForUser(user, otp);
		String url = "http://localhost:8089/forget-password/confirm-password?token=" + otp;
		String subject = "Change Password For JobPost";
		Context context = new Context();
		context.setVariable("url", url);
		String content = templateEngine.process("forgot-password", context);

		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setSubject(subject);
		helper.setText(content, true);
		helper.setTo(user.getAccount().getEmail());
		helper.setFrom(env.getProperty("spring.mail.username"), "Recruiment Manager");

		javaMailSender.send(message);

		return GenericResponse.builder().success(true).message("Please check your email to reset your password!")
				.result("Send Otp successfully!").statusCode(HttpStatus.OK.value()).build();
	}

	@PutMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestParam("token") String token,
			@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
		String result = userService.validatePasswordResetOtp(token);
		if (result == null) {
			User user = userService.getUserByPasswordResetOtp(token)
					.orElseThrow(() -> new UserNotFoundException("User Not Found")).getUser();
			userService.changeUserPassword(user, passwordResetRequest.getNewPassword(),
					passwordResetRequest.getConfirmPassword());
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Reset password successful")
					.result(null).statusCode(200).build());
		} else {
			return new ResponseEntity(GenericResponse.builder().success(false).message(result).result(null)
					.statusCode(HttpStatus.BAD_REQUEST.value()).build(), HttpStatus.BAD_REQUEST);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<Object> updateUser(@RequestBody @Valid UserUpdateRequest request,
			@RequestHeader("Authorization") String authorizationHeader, BindingResult bindingResult) throws Exception {
		if (bindingResult.hasErrors()) {
			throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
		}

		String token = authorizationHeader.substring(7);
		String userIdFromToken = jwtTokenProvider.getUserIdFromJwt(token);

		return userService.updateProfile(userIdFromToken, request);

	}

	@PutMapping("/avatar")
	public ResponseEntity<?> uploadAvatar(@RequestParam MultipartFile imageFile,
			@RequestHeader("Authorization") String token) throws IOException {

		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);

		User user = userService.findById(userId).get();
		String avatarOld = user.getProfile().getAvatar();

		// upload new avatar
		user.getProfile().setAvatar(cloudinaryService.uploadImage(imageFile));
		userService.save(user);

		// delete old avatar
		if (avatarOld != null) {
			cloudinaryService.deleteImage(avatarOld);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully")
				.result(user.getProfile().getAvatar()).statusCode(HttpStatus.OK.value()).build());
	}
}