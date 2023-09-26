package vn.iostar.service.impl;



import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iostar.dto.GroupPostResponse;
import vn.iostar.entity.PostGroup;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.service.PostGroupService;

@Service
public class PostGroupServiceImpl implements PostGroupService{

	@Autowired
	PostGroupRepository postGroupRepository;

	@Override
	public Optional<PostGroup> findById(Integer id) {
		return postGroupRepository.findById(id);
	}

	@Override
	public List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable) {
		return postGroupRepository.findPostGroupInfoByUserId(userId, pageable);
	}


}
