package vn.iostar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.entity.Account;
import vn.iostar.entity.User;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.service.AccountService;
import vn.iostar.service.EmailVerificationService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	EmailVerificationService emailVerificationService;

	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Override
	public ResponseEntity<GenericResponse> userRegister(RegisterRequest registerRequest) {
		if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
			throw new RuntimeException("Password must be between 8 and 32 characters long");

		Optional<Account> userOptional = findByPhone(registerRequest.getPhone());
		if (userOptional.isPresent())
			return ResponseEntity.status(409)
					.body(GenericResponse.builder().success(false).message("Phone number already in use").result(null)
							.statusCode(HttpStatus.CONFLICT.value()).build());

		userOptional = findByEmail(registerRequest.getEmail());
		if (userOptional.isPresent())
			return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
					.message("Email already in use").result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
			return ResponseEntity.status(409)
					.body(GenericResponse.builder().success(false).message("Password and confirm password do not match")
							.result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		saveUserAndAccount(registerRequest);
		emailVerificationService.sendOtp(registerRequest.getEmail());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Sign Up Success").result(null)
				.statusCode(200).build());
	}

	public <S extends Account> S save(S entity) {
		return accountRepository.save(entity);
	}

	private Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	private Optional<Account> findByPhone(String phone) {
		return accountRepository.findByPhone(phone);
	}

	@Override
	public String validateVerificationAccount(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveUserAndAccount(RegisterRequest registerRequest) {
		User user = new User();
		user.setPhone(registerRequest.getPhone());
		user.setUserName(registerRequest.getFullName());
		userRepository.save(user);

		Account account = new Account();
		account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		account.setPhone(registerRequest.getPhone());
		account.setEmail(registerRequest.getEmail());

		Date createDate = new Date();
		account.setCreatedAt(createDate);
		account.setUpdatedAt(createDate);

		account.setUser(user);
		accountRepository.save(account);
	}
}
