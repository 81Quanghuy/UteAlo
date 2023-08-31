package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;

import vn.iostar.entity.User;

public interface UserService {

	void deleteAll();

	void delete(User entity);

	void deleteById(String id);

	long count();

	<S extends User> long count(Example<S> example);

	Optional<User> findById(String id);

	List<User> findAll();

	<S extends User> S save(S entity);

}
