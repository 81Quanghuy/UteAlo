package vn.iostar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iostar.entity.PostGroupMember;
import vn.iostar.repository.PostGroupMemberRepository;
import vn.iostar.service.PostGroupMemberService;

@Service
public class PostGroupMemberServiceImpl implements PostGroupMemberService {

	@Autowired
	PostGroupMemberRepository groupMemberRepository;

	@Override
	public <S extends PostGroupMember> S save(S entity) {
		return groupMemberRepository.save(entity);
	}

	@Override
	public List<PostGroupMember> findAll() {
		return groupMemberRepository.findAll();
	}

	@Override
	public Optional<PostGroupMember> findById(Integer id) {
		return groupMemberRepository.findById(id);
	}

	@Override
	public long count() {
		return groupMemberRepository.count();
	}

	@Override
	public void delete(PostGroupMember entity) {
		groupMemberRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		groupMemberRepository.deleteAll();
	}
	
}
