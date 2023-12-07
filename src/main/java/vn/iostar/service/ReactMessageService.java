package vn.iostar.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import vn.iostar.dto.ReactDTO;
import vn.iostar.entity.ReactMessage;

public interface ReactMessageService {

	void deleteAll();
	
	void deleteById(String id);

	long count();

	Optional<ReactMessage> findById(String id);

	List<ReactMessage> findAll();

	<S extends ReactMessage> S save(S entity);

	ReactMessage saveReactDTO(ReactDTO react);

	List<ReactMessage> findReactMessageByCreateAt(Date createAt);

}
