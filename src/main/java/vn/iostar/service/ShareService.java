package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.SharePostRequestDTO;
import vn.iostar.dto.SharesResponse;
import vn.iostar.entity.Share;
import vn.iostar.entity.User;

public interface ShareService {

	void deleteAll();

	void delete(Share entity);

	long count();

	Optional<Share> findById(Integer id);

	List<Share> findAll();

	<S extends Share> S save(S entity);

	ResponseEntity<GenericResponse> getShare(Integer shareId);
	
	ResponseEntity<Object> sharePost(String token,SharePostRequestDTO requestDTO);
	
	ResponseEntity<Object> updateSharePost(Integer shareId, String content,String currentUserId);
	
	ResponseEntity<GenericResponse> deleteSharePost(Integer shareId,String token,String userId);
	
	SharesResponse getSharePost(Share share);
	
	public List<SharesResponse> findUserSharePosts(String userId);
	
	List<SharesResponse> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(User user);
	
	List<SharesResponse> findPostGroupShares(Integer postGroupId);
	
	ResponseEntity<GenericResponse> getGroupSharePosts(Integer postGroupId);

	
}
