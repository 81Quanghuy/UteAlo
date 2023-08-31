package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.entity.Account;
import vn.iostar.entity.User;
import vn.iostar.entity.VerificationToken;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.repository.VerificationTokenRepository;
import vn.iostar.service.AccountService;
import vn.iostar.service.EmailVerificationService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	EmailVerificationService emailVerificationService;
	
	@Autowired
    VerificationTokenRepository tokenRepository;

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
					.body(GenericResponse.builder().success(true).message("Phone number already in use").result(null)
							.statusCode(HttpStatus.CONFLICT.value()).build());

		userOptional = findByEmail(registerRequest.getEmail());
		if (userOptional.isPresent())
			return ResponseEntity.status(409).body(GenericResponse.builder().success(true)
					.message("Email already in use").result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
			return ResponseEntity.status(409)
					.body(GenericResponse.builder().success(true).message("Password and confirm password do not match")
							.result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		Account account = new Account();
		account.setPassword(registerRequest.getPassword());
		account.setPhone(registerRequest.getPhone());
		account.setEmail(registerRequest.getEmail());
		User user = new User();
		user.setPhone(registerRequest.getPhone());
		user.setUserName(registerRequest.getFullName());

		save(account);
		userRepository.save(user);

		emailVerificationService.sendOtp(registerRequest.getEmail());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Sign Up Success").result(null)
				.statusCode(200).build());
	}

	public <S extends Account> S save(S entity) {
		return accountRepository.save(entity);
	}

	public Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	public Optional<Account> findByPhone(String phone) {
		return accountRepository.findByPhone(phone);
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




}
