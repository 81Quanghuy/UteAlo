package vn.iostar.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;


import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;

public interface PostService {

	void deleteAll();

	void delete(Post entity);

	void deleteById(Integer id);

	long count();

	List<Post> findAll();

	Optional<Post> findById(Integer id);

	<S extends Post> S save(S entity);

	ResponseEntity<GenericResponse> getPost(Integer postId);

	ResponseEntity<Object> updatePost(Integer postId, PostUpdateRequest request,String currentUserId) throws Exception;

	ResponseEntity<GenericResponse> deletePost(Integer postId,String token,String userId);
	
	ResponseEntity<Object> createUserPost(String token,CreatePostRequestDTO requestDTO);

	public List<PostsResponse> findUserPosts(String userId);
	
	List<PostsResponse> findPostsByUserAndFriendsAndGroupsOrderByPostTimeDesc(User user);

	PostsResponse getPost(Post post);
	
	List<String> findAllPhotosByUserIdOrderByPostTimeDesc(String userId);
	
	Page<String> findLatestPhotosByUserId(String userId, int page, int size);


}
