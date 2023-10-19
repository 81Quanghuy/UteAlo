package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import vn.iostar.entity.PostGroupRequest;

public interface PostGroupRequestService {

	void deleteAll();

	void delete(PostGroupRequest entity);

	long count();

	Optional<PostGroupRequest> findById(String id);

	List<PostGroupRequest> findAll();

	<S extends PostGroupRequest> S save(S entity);

}
