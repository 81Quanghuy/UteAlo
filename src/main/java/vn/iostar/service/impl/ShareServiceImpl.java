package vn.iostar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.SharePostRequestDTO;
import vn.iostar.dto.ShareResponse;
import vn.iostar.dto.SharesResponse;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.Share;
import vn.iostar.entity.User;
import vn.iostar.repository.CommentRepository;
import vn.iostar.repository.LikeRepository;
import vn.iostar.repository.ShareRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.PostService;
import vn.iostar.service.ShareService;
import vn.iostar.service.UserService;

@Service
public class ShareServiceImpl implements ShareService {

	@Autowired
	ShareRepository shareRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserService userService;

	@Autowired
	PostService postService;
	
	@Autowired
	PostGroupService postGroupService;

	@Autowired
	LikeRepository likeRepository;

	@Autowired
	CommentRepository commentRepository;

	@Override
	public <S extends Share> S save(S entity) {
		return shareRepository.save(entity);
	}

	@Override
	public List<Share> findAll() {
		return shareRepository.findAll();
	}

	@Override
	public Optional<Share> findById(Integer id) {
		return shareRepository.findById(id);
	}

	@Override
	public long count() {
		return shareRepository.count();
	}

	@Override
	public void delete(Share entity) {
		shareRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		shareRepository.deleteAll();
	}

	@Override
	public ResponseEntity<GenericResponse> getShare(Integer shareId) {
		Optional<Share> share = shareRepository.findById(shareId);
		if (share.isEmpty())
			throw new RuntimeException("Share not found");
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
				.result(new ShareResponse(share.get())).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO) {
		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);
		if (user.isEmpty())
			return ResponseEntity.badRequest().body("User not found");
		Optional<Post> post = postService.findById(requestDTO.getPostId());
		if (post.isEmpty())
			return ResponseEntity.badRequest().body("Post not found");

		Share share = new Share();
		share.setContent(requestDTO.getContent());
		share.setCreateAt(new Date());
		share.setUpdateAt(new Date());
		share.setPost(post.get());
		share.setUser(user.get());
		Optional<PostGroup> postGroup = postGroupService.findById(requestDTO.getPostGroupId());
		if (postGroup.isPresent()) {
			share.setPostGroup(postGroup.get());
		}
		save(share);
		SharesResponse sharesResponse = new SharesResponse(share);
		List<Integer> count = new ArrayList<>();
		sharesResponse.setComments(count);
		sharesResponse.setLikes(count);

		GenericResponse response = GenericResponse.builder().success(true).message("Share Post Successfully")
				.result(sharesResponse).statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Object> updateSharePost(Integer shareId, String content, String currentUserId) {
		Optional<Share> shareOp = findById(shareId);
		if (shareOp.isEmpty())
			return ResponseEntity.badRequest().body("Share post doesn't exist");
		if (!currentUserId.equals(shareOp.get().getUser().getUserId()))
			return ResponseEntity.badRequest().body("Update denied");
		Share share = shareOp.get();
		share.setContent(content);
		share.setUpdateAt(new Date());
		save(share);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Update successful").result(null)
				.statusCode(200).build());
	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> deleteSharePost(Integer shareId, String token, String userId) {
		String jwt = token.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(jwt);
		if (!currentUserId.equals(userId.replaceAll("^\"|\"$", ""))) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
		}

		Optional<Share> optionalShare = findById(shareId);

		if (optionalShare.isPresent()) {
			Share share = optionalShare.get();

			shareRepository.delete(share);
			
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", null, HttpStatus.OK.value()));
		}
		// Khi không tìm thấy bài post với id
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found share post!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public SharesResponse getSharePost(Share share) {
		SharesResponse sharesResponse = new SharesResponse(share);
		sharesResponse.setComments(getIdComment(share.getComments()));
		sharesResponse.setLikes(getIdLikes(share.getLikes()));
		return sharesResponse;
	}
	
	private List<Integer> getIdLikes(List<Like> likes) {
		List<Integer> idComments = new ArrayList<>();
		for (Like like : likes) {
			idComments.add(like.getLikeId());
		}
		return idComments;
	}

	private List<Integer> getIdComment(List<Comment> comments) {
		List<Integer> idComments = new ArrayList<>();
		for (Comment cmt : comments) {
			idComments.add(cmt.getCommentId());
		}
		return idComments;
	}

	@Override
	public List<SharesResponse> findUserSharePosts(String userId) {
		List<Share> userSharePosts = shareRepository.findByUserUserId(userId);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for(Share share : userSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public List<SharesResponse> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(User user) {
		List<Share> userSharePosts = shareRepository.findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(user);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for(Share share : userSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public List<SharesResponse> findPostGroupShares(Integer postGroupId) {
		List<Share> groupSharePosts = shareRepository.findByPostGroupPostGroupId(postGroupId);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for(Share share : groupSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public ResponseEntity<GenericResponse> getGroupSharePosts(Integer postGroupId) {
		List<SharesResponse> groupSharePosts = findPostGroupShares(postGroupId);
		
		if (groupSharePosts.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No posts found for this group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Retrieved group posts successfully").result(groupSharePosts)
					.statusCode(HttpStatus.OK.value()).build());
		}
	}

}
