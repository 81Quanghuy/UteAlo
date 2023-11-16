package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

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
	List<Comment> findAllAndCommentReplyIsNullByOrderByCreateTimeDesc();
}
