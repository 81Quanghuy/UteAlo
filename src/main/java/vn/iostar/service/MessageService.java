package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import vn.iostar.entity.Message;

public interface MessageService {

	void deleteAll();

	void delete(Message entity);

	void deleteById(String id);

	long count();

	Optional<Message> findById(String id);

	List<Message> findAll();

	<S extends Message> S save(S entity);

}
