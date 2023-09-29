package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;




@Repository
public interface LikeRepository extends JpaRepository<Like, Integer>{
	
	List<Like> findByPostPostId(int postId);
	
	Optional<Like> findByPostAndUser(Post post, User user);
	
	List<Like> findByCommentCommentId(int postId);
	
	Optional<Like> findByCommentAndUser(Comment comment, User user);
	
	void deleteByCommentCommentId(Integer commentId);
	
	void deleteByPostPostId(Integer postId);
	
	void deleteByCommentPostPostId(Integer postId);
	
}
