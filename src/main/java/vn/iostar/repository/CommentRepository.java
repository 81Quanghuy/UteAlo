package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Comment;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>{
	List<Comment> findByPostPostId(int postId);
	Optional<Comment> findByPostAndUser(Post post, User user);
	void deleteByPostPostId(Integer postId);
}
