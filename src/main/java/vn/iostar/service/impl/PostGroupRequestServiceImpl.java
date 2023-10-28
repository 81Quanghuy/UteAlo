package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iostar.entity.PostGroupRequest;
import vn.iostar.repository.PostGroupRequestRepository;
import vn.iostar.service.PostGroupRequestService;

@Service
public class PostGroupRequestServiceImpl implements PostGroupRequestService {

	@Autowired
	PostGroupRequestRepository postGroupRequestRepository;

	@Override
	public <S extends PostGroupRequest> S save(S entity) {
		return postGroupRequestRepository.save(entity);
	}

	@Override
	public List<PostGroupRequest> findAll() {
		return postGroupRequestRepository.findAll();
	}

	@Override
	public Optional<PostGroupRequest> findById(String id) {
		return postGroupRequestRepository.findById(id);
	}

	@Override
	public long count() {
		return postGroupRequestRepository.count();
	}

	@Override
	public List<PostGroupRequest> findByIsAcceptAndPostGroupPostGroupId(Integer postGroupId, Boolean isAccept) {
		return postGroupRequestRepository.findByIsAcceptAndPostGroupPostGroupId(isAccept, postGroupId);
	}

	@Override
	public void delete(PostGroupRequest entity) {
		postGroupRequestRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		postGroupRequestRepository.deleteAll();
	}

}
