package vn.iostar.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.LoginDTO;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.dto.TokenRequest;
import vn.iostar.entity.Account;
import vn.iostar.entity.RefreshToken;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.RefreshTokenRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.security.UserDetail;
import vn.iostar.service.AccountService;
import vn.iostar.service.RefreshTokenService;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AccountService userService;

	@Autowired
	RefreshTokenService refreshTokenService;

	@Autowired
	TemplateEngine templateEngine;

	@Autowired
	RefreshTokenRepository refreshTokenRepository;

	@Autowired
	AccountRepository userRepository;

	@PostMapping("/login")
	@Transactional
	public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {

		if (userService.findByEmail(loginDTO.getCredentialId()).isEmpty()
				&& userService.findByPhone(loginDTO.getCredentialId()).isEmpty()) {
			return ResponseEntity.ok().body(GenericResponse.builder().success(false).message("not found user")
					.result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		Optional<Account> optionalUser = userService.findByEmail(loginDTO.getCredentialId());
		if (optionalUser.isPresent() && !optionalUser.get().isVerified()) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(GenericResponse.builder().success(false).message("Your account is not verified!").result(null)
							.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());
		}
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDTO.getCredentialId(), loginDTO.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetail userDetail = (UserDetail) authentication.getPrincipal();
		String accessToken = jwtTokenProvider.generateAccessToken(userDetail);
		RefreshToken refreshToken = new RefreshToken();
		String token = jwtTokenProvider.generateRefreshToken(userDetail);
		refreshToken.setToken(token);
		refreshToken.setUser(userDetail.getUser().getUser());
		// invalid all refreshToken before
		refreshTokenService.revokeRefreshToken(userDetail.getUserId());
		refreshTokenService.save(refreshToken);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("accessToken", accessToken);
		tokenMap.put("refreshToken", token);
		tokenMap.put("userId", userDetail.getUserId());
		tokenMap.put("roleName", userDetail.getUser().getUser().getRole().getRoleName().name());

		if (optionalUser.isPresent()) {
			optionalUser.get().setLastLoginAt(new Date());
			userService.save(optionalUser.get());
		}

		return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Login successfully!")
				.result(tokenMap).statusCode(HttpStatus.OK.value()).build());

	}

	@PostMapping("/register")
	public ResponseEntity<GenericResponse> registerProcess(@RequestBody @Valid RegisterRequest registerRequest,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

			return ResponseEntity.status(500)
					.body(new GenericResponse(false, errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
		} else {
			return userService.userRegister(registerRequest);
		}

	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam("refreshToken") String refreshToken) {
		String accessToken = authorizationHeader.substring(7);

		if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromJwt(refreshToken))) {
			return refreshTokenService.logout(refreshToken);
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(GenericResponse.builder().success(false).message("Logout failed!")
						.result("Please login before logout!").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
	}

	@PostMapping("/logout-all")
	public ResponseEntity<GenericResponse> logoutAll(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam("refreshToken") String refreshToken) {
		String accessToken = authorizationHeader.substring(7);
		if (jwtTokenProvider.getUserIdFromJwt(accessToken).equals(jwtTokenProvider.getUserIdFromJwt(refreshToken))) {
			String userId = jwtTokenProvider.getUserIdFromJwt(refreshToken);
			refreshTokenService.revokeRefreshToken(userId);
			SecurityContextHolder.clearContext();
			return ResponseEntity.ok().body(GenericResponse.builder().success(true).message("Logout successfully!")
					.result("").statusCode(HttpStatus.OK.value()).build());
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(GenericResponse.builder().success(false).message("Logout failed!")
						.result("Please login before logout!").statusCode(HttpStatus.UNAUTHORIZED.value()).build());
	}

	@PostMapping("/refresh-access-token")
	public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody TokenRequest tokenRequest) {
		String refreshToken = tokenRequest.getRefreshToken();
		return refreshTokenService.refreshAccessToken(refreshToken);
	}

	@GetMapping(value = "/registration-confirm", produces = MediaType.TEXT_HTML_VALUE)
	public String confirmRegistration(@RequestParam("token") final String token) {
		String result = userService.validateVerificationAccount(token);
		Context context = new Context();
		context.setVariable("result", result);
		return templateEngine.process("result-confirm", context);
	}

}
