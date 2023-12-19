package vn.iostar.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Comment;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

	List<Comment> findByPostPostIdAndCommentReplyIsNullOrderByCreateTimeDesc(int postId);

	List<Comment> findByShareShareIdAndCommentReplyIsNullOrderByCreateTimeDesc(int shareId);

	Optional<Comment> findByPostAndUser(Post post, User user);

	void deleteByPostPostId(Integer postId);

	// Lấy danh sách các comment reply của một comment theo commentId, sắp xếp theo
	// thời gian tạo giảm dần
	@Query("SELECT c FROM Comment c WHERE c.commentReply.commentId = :commentId ORDER BY c.createTime DESC")
	List<Comment> findCommentRepliesByCommentIdOrderByCreateTimeDesc(int commentId);

	void deleteByShareShareId(Integer shareId);

	// Lấy tất cả comment trong hệ thống
	Page<Comment> findAllByOrderByCreateTimeDesc(Pageable pageable);

	// Đếm số lượng comment trong khoảng thời gian
	long countByCreateTimeBetween(Date startDateAsDate, Date endDateAsDate);

	// Lấy những bình luận trong khoảng thời gian
	List<Comment> findByCreateTimeBetween(Date startDate, Date endDate);

	// Đếm số lượng bình luận của một người dùng cụ thể
	Long countCommentsByUser(User user);

	// Định nghĩa phương thức để tìm tất cả bình luận của một userId và sắp xếp theo
	// thời gian đăng bài giảm dần
	Page<Comment> findAllByUser_UserIdOrderByCreateTimeDesc(String userId, Pageable pageable);
	
	 // Định nghĩa hàm lấy những bài post của 1 user trong 1 tháng
    Page<Comment> findByUserAndCreateTimeBetween(User user, Date start, Date end, Pageable pageable);
	
	// Đếm số lượng bình luận của người dùng trong 1 tháng
	long countByUserAndCreateTimeBetween(User user, Date start, Date end);

}
