package vn.iostar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Post;

public interface PostService {

	void deleteAll();

	void delete(Post entity);

	void deleteById(String id);

	long count();

	List<Post> findAll();

	Optional<Post> findById(String id);

	<S extends Post> S save(S entity);

	ResponseEntity<GenericResponse> getPost(String userId);

	ResponseEntity<Object> updatePost(String postId, PostUpdateRequest request) throws Exception;

	ResponseEntity<GenericResponse> deletePost(String idFromToken);
	
	ResponseEntity<Object> createUserPost(String token,CreatePostRequestDTO requestDTO );

	public List<PostsResponse> findUserPosts(String userId);
	
	Post likePost(String postId, String userId);

	PostsResponse getPost(Post post);


}
