package vn.iostar.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Post;

public interface PostService {

	void deleteAll();

	void delete(Post entity);

	void deleteById(Integer id);

	long count();

	List<Post> findAll();

	Optional<Post> findById(Integer id);

	<S extends Post> S save(S entity);

	ResponseEntity<GenericResponse> getPost(Integer postId);

	ResponseEntity<Object> updatePost(Integer postId, PostUpdateRequest request) throws Exception;

	ResponseEntity<GenericResponse> deletePost(Integer postId,String token,String userId);
	
	ResponseEntity<Object> createUserPost(String token,CreatePostRequestDTO requestDTO);

	public List<PostsResponse> findUserPosts(String userId);

	PostsResponse getPost(Post post);


}
