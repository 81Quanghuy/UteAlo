package vn.iostar.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import vn.iostar.dto.RegisterRequest;
import vn.iostar.dto.TokenRequest;
import vn.iostar.security.JwtTokenProvider;
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

	@PostMapping("/register")
	public ResponseEntity<GenericResponse> registerProcess(@RequestBody @Valid RegisterRequest registerRequest,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

			return ResponseEntity.status(500)
					.body(new GenericResponse(false, errorMessage, null, HttpStatus.INTERNAL_SERVER_ERROR.value()));
		}
		return userService.userRegister(registerRequest);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam("refreshToken") String refreshToken) {
		String accessToken = authorizationHeader.substring(7);
		if (jwtTokenProvider.getUserIdFromJwt(accessToken)
				.equals(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken))) {
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
		if (jwtTokenProvider.getUserIdFromJwt(accessToken)
				.equals(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken))) {
			String userId = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
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
