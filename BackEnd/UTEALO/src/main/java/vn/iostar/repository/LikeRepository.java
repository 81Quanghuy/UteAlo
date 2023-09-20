package vn.iostar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;


@Repository
public interface LikeRepository extends JpaRepository<Like, String>{
	List<Like> findByPostPostId(int postId);
	Optional<Like> findByPostAndUser(Post post, User user);
}
