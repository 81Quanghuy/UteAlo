package vn.iostar.controller.user;


import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.PostUpdateRequest;
import vn.iostar.dto.PostsResponse;
import vn.iostar.entity.Post;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostService;


@RestController
@RequestMapping("/api/v1/post")
public class PostController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;


	@GetMapping("/{postId}")
	public ResponseEntity<GenericResponse> getPost(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postId") Integer postId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<Post> post = postService.findById(postId);
		PostsResponse userPosts = postService.getPost(post.get());
		if (post.isEmpty()) {
			throw new RuntimeException("Post not found.");
		} else if (currentUserId.equals(post.get().getUser().getUserId())) {
			return ResponseEntity.ok(
					GenericResponse.builder().success(true).message("Retrieving post successfully and access update")
							.result(userPosts).statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieving post successfully and access update denied")
					.result(userPosts).statusCode(HttpStatus.OK.value()).build());
		}
	}
	
	@GetMapping("/{userId}/posts")
	public ResponseEntity<GenericResponse> getUserPosts(@RequestHeader("Authorization") String authorizationHeader,
	        @PathVariable("userId") String userId) {
	    String token = authorizationHeader.substring(7);
	    String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
	    List<PostsResponse> userPosts = postService.findUserPosts(userId);
	    
	     	
	    if(userId.isEmpty()) {
	    	throw new RuntimeException("User not found.");
	    }   
	    else if (userPosts.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(GenericResponse.builder().success(false).message("No posts found for this user").statusCode(HttpStatus.NOT_FOUND.value()).build());
	    }
	    else if (!currentUserId.equals(userId)) {
	    	return ResponseEntity.ok(
		            GenericResponse.builder().success(true).message("Retrieved user posts successfully and access update denied")
		                    .result(userPosts).statusCode(HttpStatus.OK.value()).build());
	    } else {
	    	 return ResponseEntity.ok(
	 	            GenericResponse.builder().success(true).message("Retrieved user posts successfully and access update")
	 	                    .result(userPosts).statusCode(HttpStatus.OK.value()).build());
	    }    
	}


	@PutMapping("/update/{postId}")
	public ResponseEntity<Object> updateUser(@RequestBody @Valid PostUpdateRequest request,
			@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") Integer postId,
			BindingResult bindingResult) throws Exception {
		if (bindingResult.hasErrors()) {
			throw new Exception(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
		}

		return postService.updatePost(postId, request);

	}

	@PutMapping("/delete/{postId}")
	public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String token,
			@PathVariable("postId") Integer postId,@RequestBody String userId) {
		return postService.deletePost(postId,token,userId);

	}

	@PostMapping("/create")
	public ResponseEntity<Object> createPost(@RequestBody CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return postService.createUserPost(token, requestDTO);
	}
	
	 

}
