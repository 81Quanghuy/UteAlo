package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.Share;
import vn.iostar.entity.User;

public interface LikeService {

	void deleteAll();

	void delete(Like entity);

	long count();

	Optional<Like> findById(Integer id);

	<S extends Like> Page<S> findAll(Example<S> example, Pageable pageable);

	List<Like> findAll();

	<S extends Like> S save(S entity);
	
	ResponseEntity<GenericResponse> getLikeOfPost(int postId);
	
	ResponseEntity<GenericResponse> getLikeOfShare(int shareId);
	
	ResponseEntity<GenericResponse> getLikeOfComment(int commentId);
	
	ResponseEntity<Object> toggleLikePost(String token,Integer postId );
	
	ResponseEntity<Object> toggleLikeShare(String token,Integer shareId );
	
	Optional<Like> findByPostAndUser(Post post, User user);
	
	Optional<Like> findByShareAndUser(Share share, User user);
	
	ResponseEntity<GenericResponse> getCountLikeOfPost(int postId);
	
	ResponseEntity<GenericResponse> getCountLikeOfComment(int commentId);
	
	ResponseEntity<Object> toggleLikeComment(String token,Integer commentId );
	
	Optional<Like> findByCommentAndUser(Comment comment, User user);
	
	ResponseEntity<Object> checkUserLikePost(String token,Integer postId );
	
	ResponseEntity<Object> checkUserLikeShare(String token,Integer shareId );
	
	ResponseEntity<Object> checkUserLikeComment(String token,Integer commentId );
	
	// Lấy danh sách những người đã like bài post
	ResponseEntity<Object> listUserLikePost(Integer postId );
	
	// Lấy danh sách những người đã like bài share
	ResponseEntity<Object> listUserLikeShare(Integer shareId );
	
	// Lấy danh sách những người đã like comment
	ResponseEntity<Object> listUserLikeComment(Integer commentId );
}
