package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

	@Autowired
	EmailVerificationService emailVerificationService;
	
	@Autowired
    VerificationTokenRepository tokenRepository;

	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
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
