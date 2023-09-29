package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import vn.iostar.dto.GroupPostResponse;
import vn.iostar.entity.PostGroup;

public interface PostGroupService {

	Optional<PostGroup> findById(Integer id);

	List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable);
}
