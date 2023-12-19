package vn.iostar.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Streamable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CommentUpdateRequest;
import vn.iostar.dto.CommentsResponse;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.CreateCommentShareRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
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
	ResponseEntity<GenericResponse> deleteCommentByAdmin(Integer commentId, String authorizationHeader);

	// Lấy tất cả bài post trong hệ thống
	Streamable<Object> findAllComments(int page, int itemsPerPage);

	ResponseEntity<GenericResponseAdmin> getAllComments(String authorizationHeader, int page, int itemsPerPage);

	// Đếm số lượng comment từng tháng trong năm
	Map<String, Long> countCommentsByMonthInYear();

	// Đếm số lượng comment của 1 user từng tháng trong năm
	Map<String, Long> countCommentsByUserMonthInYear(String userId);

	// Đếm số lượng comment trong 1 năm
	long countCommentsInOneYearFromNow();

	// Thống kê bình luận trong ngày hôm nay
	List<CommentsResponse> getCommentsToday();

	// Thống kê bình luận trong 7 ngày
	List<CommentsResponse> getCommentsIn7Days();

	// Thống kê bình luận trong 1 tháng
	List<CommentsResponse> getCommentsIn1Month();

	// Lấy tất cả bình luận của 1 user có phân trang
	Streamable<Object> findAllCommentsByUserId(int page, int itemsPerPage, String userId);

	// Lấy tất cả bài comment của 1 user trong 1 tháng có phân trang
	Streamable<Object> findAllCommentsInMonthByUserId(int page, int itemsPerPage, String userId);
}
