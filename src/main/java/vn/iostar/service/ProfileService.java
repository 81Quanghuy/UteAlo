package vn.iostar.service;

import java.util.List;

import vn.iostar.entity.Profile;

public interface ProfileService {

	void delete(Profile entity);

	long count();

	List<Profile> findAll();

	<S extends Profile> S save(S entity);

}
