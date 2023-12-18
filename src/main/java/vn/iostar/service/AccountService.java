package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.entity.Account;


public interface AccountService {
	List<Account> findAll();

	ResponseEntity<GenericResponse> userRegister(@Valid RegisterRequest registerRequest);

	String validateVerificationAccount(String token);
	
	Optional<Account> findByPhone(String phone);
	
	Optional<Account> findByEmail(String email);
	
	<S extends Account> S save(S entity);

	<S extends Account> List<S> saveAll(Iterable<S> entities);

}
