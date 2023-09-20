package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CreateLikePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;

public interface LikeService {

	void deleteAll();

	void delete(Like entity);

	long count();

	Optional<Like> findById(String id);

	<S extends Like> Page<S> findAll(Example<S> example, Pageable pageable);

	List<Like> findAll();

	<S extends Like> S save(S entity);
	
	ResponseEntity<GenericResponse> getLikeOfPost(int postId);
	
	ResponseEntity<Object> toggleLike(String token,CreateLikePostRequestDTO requestDTO );
	
	Optional<Like> findByPostAndUser(Post post, User user);
	
	ResponseEntity<GenericResponse> getCountLikeOfPost(int postId);
}
