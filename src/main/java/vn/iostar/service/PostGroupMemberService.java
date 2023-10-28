package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import vn.iostar.entity.PostGroupMember;

public interface PostGroupMemberService {

	void deleteAll();

	void delete(PostGroupMember entity);

	long count();

	Optional<PostGroupMember> findById(Integer id);

	List<PostGroupMember> findAll();

	<S extends PostGroupMember> S save(S entity);

}
