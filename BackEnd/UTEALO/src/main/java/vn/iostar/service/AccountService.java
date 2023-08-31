package vn.iostar.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.entity.Account;

public interface AccountService {
	List<Account> findAll();

	ResponseEntity<GenericResponse> userRegister(@Valid RegisterRequest registerRequest);

	String validateVerificationAccount(String token);
}
