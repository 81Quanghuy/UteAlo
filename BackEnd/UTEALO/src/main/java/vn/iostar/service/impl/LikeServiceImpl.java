package vn.iostar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import vn.iostar.dto.CreateLikePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.LikePostResponse;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.User;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.LikeService;
import vn.iostar.service.PostService;
import vn.iostar.service.UserService;

@Service
public class LikeServiceImpl implements LikeService {

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	PostService postService;

	@Autowired
	UserService userService;

	@Override
	public <S extends Like> S save(S entity) {
		return likeRepository.save(entity);
	}

	@Override
	public List<Like> findAll() {
		return likeRepository.findAll();
	}

	@Override
	public <S extends Like> Page<S> findAll(Example<S> example, Pageable pageable) {
		return likeRepository.findAll(example, pageable);
	}

	@Override
	public Optional<Like> findById(String id) {
		return likeRepository.findById(id);
	}

	@Override
	public long count() {
		return likeRepository.count();
	}

	@Override
	public void delete(Like entity) {
		likeRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		likeRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getLikeOfPost(int postId) {
		List<Like> likes = likeRepository.findByPostPostId(postId);
		if (likes.isEmpty())
			throw new RuntimeException("This post has no like");
		List<LikePostResponse> likePostResponses = new ArrayList<>();
		for(Like like : likes) {
			likePostResponses.add(new LikePostResponse(like));
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving like of post successfully")
				.result(likePostResponses)
				.statusCode(HttpStatus.OK.value()).build());
	}
	
	@Override
	public ResponseEntity<GenericResponse> getCountLikeOfPost(int postId) {
		List<Like> likes = likeRepository.findByPostPostId(postId);
		if(likes.isEmpty())
			throw new RuntimeException("This post has no like");
		List<LikePostResponse> likePostResponses = new ArrayList<>();
		for(Like like : likes) {
			likePostResponses.add(new LikePostResponse(like));
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving like of post successfully")
				.result(likePostResponses.size())
				.statusCode(HttpStatus.OK.value()).build());
	}


	@Override
	public ResponseEntity<Object> toggleLike(String token, CreateLikePostRequestDTO requestDTO) {
	    String jwt = token.substring(7);
	    String userId = jwtTokenProvider.getUserIdFromJwt(jwt);

	    if (userId.isEmpty()) {
	        return ResponseEntity.badRequest().body("User not found");
	    }

	    Optional<Post> post = postService.findById(requestDTO.getPostId());
	    Optional<User> user = userService.findById(requestDTO.getUserId());

	    if (!post.isPresent()) {
	        return ResponseEntity.badRequest().body("Post not found");
	    }

	    if (!user.isPresent()) {
	        return ResponseEntity.badRequest().body("User not found");
	    }

	    // Kiểm tra xem cặp giá trị postId và userId đã tồn tại trong bảng Like chưa
	    Optional<Like> existingLike = findByPostAndUser(post.get(), user.get());

	    if (existingLike.isPresent()) {
	        // Nếu đã tồn tại, thực hiện xóa
	        delete(existingLike.get());
	        return ResponseEntity.ok("Like removed successfully");
	    } else {
	        // Nếu chưa tồn tại, tạo và lưu Like mới
	        Like like = new Like();
	        like.setPost(post.get());
	        like.setUser(user.get());
	        like.setStatus(null); // Cập nhật status nếu cần
	        save(like);

	        GenericResponse response = GenericResponse.builder()
	            .success(true)
	            .message("Like Post Successfully")
	            .result(new LikePostResponse(like.getLikeId(), like.getPost().getPostId(), like.getUser().getUserName()))
	            .statusCode(200)
	            .build();
	        
	        return ResponseEntity.ok(response);
	    }
	}

	public Optional<Like> findByPostAndUser(Post post, User user) {
        return likeRepository.findByPostAndUser(post, user);
    }

}
