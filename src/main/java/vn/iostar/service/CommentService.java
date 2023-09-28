package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Comment;

public interface CommentService {

	void deleteAll();

	void delete(Comment entity);

	long count();

	Optional<Comment> findById(Integer id);

	<S extends Comment> Page<S> findAll(Example<S> example, Pageable pageable);

	List<Comment> findAll();

	Page<Comment> findAll(Pageable pageable);

	<S extends Comment> S save(S entity);
	
	 ResponseEntity<GenericResponse> getCommentOfPost(int postId);
	 
	 ResponseEntity<GenericResponse> getCountCommentOfPost(int postId);
	 
	 ResponseEntity<Object> createCommentPost(String token,CreateCommentPostRequestDTO requestDTO) ;

	 ResponseEntity<GenericResponse> deleteCommentOfPost(Integer commentId);
}
