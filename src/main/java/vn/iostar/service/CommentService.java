package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CommentPostResponse;
import vn.iostar.dto.CommentUpdateRequest;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.CreateCommentShareRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.ReplyCommentPostRequestDTO;
import vn.iostar.dto.ReplyCommentShareRequestDTO;
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
	
	ResponseEntity<GenericResponse> getCommentOfShare(int shareId);

	ResponseEntity<GenericResponse> getCommentReplyOfComment(int commentId);
	
	ResponseEntity<GenericResponse> getCommentReplyOfCommentShare(int commentId);

	ResponseEntity<GenericResponse> getCountCommentOfPost(int postId);

	ResponseEntity<Object> createCommentPost(String token, CreateCommentPostRequestDTO requestDTO);
	
	ResponseEntity<Object> createCommentShare(String token, CreateCommentShareRequestDTO requestDTO);

	ResponseEntity<Object> replyCommentPost(String token, ReplyCommentPostRequestDTO requestDTO);
	
	ResponseEntity<Object> replyCommentShare(String token, ReplyCommentShareRequestDTO requestDTO);

	ResponseEntity<Object> updateComment(Integer commentId, CommentUpdateRequest request, String currentUserId)
			throws Exception;

	// Xóa comment của mình hoặc trong bài post của mình
	ResponseEntity<GenericResponse> deleteCommentOfPost(Integer commentId);
	
	// Admin xóa comment trong hệ thống
	ResponseEntity<GenericResponse> deleteCommentByAdmin(Integer commentId,String authorizationHeader);
	
	// Lấy tất cả bài post trong hệ thống 
	List<CommentPostResponse> findAllComments();
	
	ResponseEntity<GenericResponse> getAllComments(String authorizationHeader);
	
}
