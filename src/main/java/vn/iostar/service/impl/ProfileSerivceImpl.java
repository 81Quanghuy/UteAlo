package vn.iostar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iostar.entity.Profile;
import vn.iostar.repository.ProfileRepository;
import vn.iostar.service.ProfileService;

@Service
public class ProfileSerivceImpl implements ProfileService{

	@Autowired
	ProfileRepository profileRepository;

	@Override
	public <S extends Profile> S save(S entity) {
		return profileRepository.save(entity);
	}

	@Override
	public List<Profile> findAll() {
		return profileRepository.findAll();
	}

	@Override
	public long count() {
		return profileRepository.count();
	}

	@Override
	public void delete(Profile entity) {
		profileRepository.delete(entity);
	}
}
