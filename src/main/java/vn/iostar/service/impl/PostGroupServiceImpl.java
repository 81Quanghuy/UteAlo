package vn.iostar.service.impl;



import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


}
