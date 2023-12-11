package vn.iostar.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.iostar.contants.PrivacyLevel;
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
		if (share.isEmpty()) {
			throw new RuntimeException("Share not found");
		}
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
		share.setCreateAt(requestDTO.getCreateAt());
		share.setUpdateAt(requestDTO.getCreateAt());
		share.setPost(post.get());
		share.setUser(user.get());
		share.setPrivacyLevel(requestDTO.getPrivacyLevel());
		if (requestDTO.getPostGroupId() != null)
			if (requestDTO.getPostGroupId() != 0) {
				Optional<PostGroup> postGroup = postGroupService.findById(requestDTO.getPostGroupId());
				postGroup.ifPresent(share::setPostGroup);
			}
		save(share);
		SharesResponse sharesResponse = new SharesResponse(share, userId);
		List<Integer> count = new ArrayList<>();
		sharesResponse.setComments(count);
		sharesResponse.setLikes(count);

		GenericResponse response = GenericResponse.builder().success(true).message("Share Post Successfully")
				.result(sharesResponse).statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId) {
		Optional<Share> shareOp = findById(Integer.valueOf(requestDTO.getShareId()));
		if (shareOp.isEmpty())
			return ResponseEntity.badRequest().body("Share post doesn't exist");
		if (!currentUserId.equals(shareOp.get().getUser().getUserId()))
			return ResponseEntity.badRequest().body("Update denied");
		Share share = shareOp.get();
		share.setContent(requestDTO.getContent());
		share.setPrivacyLevel(requestDTO.getPrivacyLevel());
		if (requestDTO.getPostGroupId() != null) {
			if (requestDTO.getPostGroupId() != 0) {
				Optional<PostGroup> poOptional = postGroupService.findById(requestDTO.getPostGroupId());
				poOptional.ifPresent(share::setPostGroup);
			}
		}

		share.setUpdateAt(requestDTO.getUpdateAt());
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
	public ResponseEntity<GenericResponse> getShareOfPostGroup(String currentUserId, Pageable pageable) {
		if (currentUserId == null)
			return ResponseEntity.badRequest().body(new GenericResponse(false, "User not found", null, 400));
		List<Share> shares = shareRepository.findAllSharesInUserGroups(currentUserId, pageable);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for (Share share : shares) {
			SharesResponse sharesResponse = new SharesResponse(share, currentUserId);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved share post successfully")
				.result(sharesResponses).statusCode(HttpStatus.OK.value()).build());

	}

	@Override
	public SharesResponse getSharePost(Share share, String currentUserId) {
		SharesResponse sharesResponse = new SharesResponse(share, currentUserId);
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
	public List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable) {
		List<PrivacyLevel> privacyLevels = Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.FRIENDS);
		List<Share> userSharePosts = shareRepository.findByUserUserIdAndPrivacyLevelInOrderByCreateAtDesc(userId,
				privacyLevels, pageable);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for (Share share : userSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share, currentUserId);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public List<SharesResponse> findMySharePosts(String currentUserId, Pageable pageable) {
		List<Share> userSharePosts = shareRepository.findByUserUserId(currentUserId, pageable);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for (Share share : userSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public List<SharesResponse> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(String userId,
			Pageable pageable) {
		List<Share> userSharePosts = shareRepository.findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(userId,
				pageable);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for (Share share : userSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share, userId);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	@Override
	public List<SharesResponse> findPostGroupShares(String currentUserId, Integer postGroupId, Pageable pageable) {
		List<Share> groupSharePosts = shareRepository.findByPostGroupPostGroupId(postGroupId, pageable);
		List<SharesResponse> sharesResponses = new ArrayList<>();
		for (Share share : groupSharePosts) {
			SharesResponse sharesResponse = new SharesResponse(share, currentUserId);
			sharesResponse.setComments(getIdComment(share.getComments()));
			sharesResponse.setLikes(getIdLikes(share.getLikes()));
			sharesResponses.add(sharesResponse);
		}
		return sharesResponses;
	}

	//
	@Override
	public ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, Integer postGroupId, Integer page,
			Integer size) {
		Pageable pageable = PageRequest.of(page, size);
		List<SharesResponse> groupSharePosts = findPostGroupShares(currentUserId, postGroupId, pageable);

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved group posts successfully")
				.result(groupSharePosts).statusCode(HttpStatus.OK.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> getTimeLineSharePosts(String currentUserId, Integer page, Integer size) {

		Optional<User> user = userService.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.ofNullable(GenericResponse.builder().success(false).message("User not found")
					.result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
		Pageable pageable = PageRequest.of(page, size);
		List<SharesResponse> userPosts = findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(currentUserId,
				pageable);
		return ResponseEntity.ok(
				GenericResponse.builder().success(true).message("Retrieved user posts successfully and access update")
						.result(userPosts).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getShare(String currentUserId, Integer shareId) {
		Optional<Share> share = shareRepository.findById(shareId);
		if (share.isEmpty())
			return ResponseEntity.ok(GenericResponse.builder().success(null).message("not found share").result(null)
					.statusCode(HttpStatus.NOT_FOUND.value()).build());

		SharesResponse sharePost = getSharePost(share.get(), currentUserId);

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
				.result(sharePost).statusCode(HttpStatus.OK.value()).build());
	}
}
