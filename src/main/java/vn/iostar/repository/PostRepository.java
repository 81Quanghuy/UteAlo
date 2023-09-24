package vn.iostar.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iostar.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer>{
	List<Post> findByUserUserId(String userId);
}
