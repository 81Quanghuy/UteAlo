package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.SharePostRequestDTO;
import vn.iostar.dto.SharesResponse;
import vn.iostar.entity.Share;

public interface ShareService {

    void deleteAll();

    void delete(Share entity);

    long count();

    Optional<Share> findById(Integer id);

    List<Share> findAll();

    <S extends Share> S save(S entity);

    ResponseEntity<GenericResponse> getShare(Integer shareId);

    ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO);

    ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId);

    ResponseEntity<GenericResponse> deleteSharePost(Integer shareId, String token, String userId);

    ResponseEntity<GenericResponse> getShareOfPostGroup(String currentUserId, Pageable pageable);

    SharesResponse getSharePost(Share share, String currentUserId);

    List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable);

    List<SharesResponse> findMySharePosts(String currentUserId, Pageable pageable);

    List<SharesResponse> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(String userId, Pageable pageable);

    List<SharesResponse> findPostGroupShares(String currentUserId, Integer postGroupId, Pageable pageable);

    ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, Integer postGroupId, Integer page, Integer size);


    ResponseEntity<GenericResponse> getTimeLineSharePosts(String currentUserId, Integer page, Integer size);

	ResponseEntity<GenericResponse> getShare(String currentUserId, Integer shareId);
}
