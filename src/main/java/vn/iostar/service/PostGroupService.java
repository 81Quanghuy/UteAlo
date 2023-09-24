package vn.iostar.service;

import java.util.Optional;

import vn.iostar.entity.PostGroup;

public interface PostGroupService{

	Optional<PostGroup> findById(Integer id);

}
