package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iostar.dto.ListUserLikePost;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.Share;
import vn.iostar.entity.User;




@Repository
public interface LikeRepository extends JpaRepository<Like, Integer>{
	
	List<Like> findByPostPostId(int postId);
	
	List<Like> findByShareShareId(int shareId);
	
	Optional<Like> findByPostAndUser(Post post, User user);
	
	Optional<Like> findByShareAndUser(Share share, User user);
	
	List<Like> findByCommentCommentId(int postId);
	
	Optional<Like> findByCommentAndUser(Comment comment, User user);
	
	void deleteByCommentCommentId(Integer commentId);
	
	void deleteByPostPostId(Integer postId);
	
	void deleteByCommentPostPostId(Integer postId);
	
	void deleteByShareShareId(int shareId);
	
	void deleteByCommentShareShareId(Integer shareId);
	
	// Lấy danh sách người dùng thích bài viết
	@Query("SELECT NEW vn.iostar.dto.ListUserLikePost(l.user.userName, l.user.userId, l.user.profile.avatar) FROM Like l WHERE l.post.postId = :postId")
    List<ListUserLikePost> findUsersLikedPost(@Param("postId") Integer postId);
	
	// Lấy danh sách người dùng thích bài viết
	@Query("SELECT NEW vn.iostar.dto.ListUserLikePost(l.user.userName, l.user.userId, l.user.profile.avatar) FROM Like l WHERE l.share.shareId = :shareId")
    List<ListUserLikePost> findUsersLikedShare(@Param("shareId") Integer shareId);
	
	// Lấy danh sách người dùng thích bài viết
	@Query("SELECT NEW vn.iostar.dto.ListUserLikePost(l.user.userName, l.user.userId, l.user.profile.avatar) FROM Like l WHERE l.comment.commentId = :commentId")
    List<ListUserLikePost> findUsersLikedComment(@Param("commentId") Integer commentId);
}
