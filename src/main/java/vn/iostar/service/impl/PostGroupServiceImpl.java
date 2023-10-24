package vn.iostar.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.iostar.contants.RoleUserGroup;
import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.InvitedPostGroupResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.dto.PostGroupResponse;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.PostGroupMember;
import vn.iostar.entity.PostGroupRequest;
import vn.iostar.entity.User;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.PostGroupMemberRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.PostGroupRequestRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.PostGroupService;

@Service
public class PostGroupServiceImpl implements PostGroupService {

	@Autowired
	PostGroupRepository postGroupRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	UserRepository userRepository;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	FriendRepository friendRepository;

	@Autowired
	PostGroupMemberRepository groupMemberRepository;

	@Autowired
	PostGroupRequestRepository postGroupRequestRepository;

	@Override
	public Optional<PostGroup> findById(Integer id) {
		return postGroupRepository.findById(id);
	}

	@Override
	public List<GroupPostResponse> findPostGroupInfoByUserId(String userId, Pageable pageable) {
		return postGroupRepository.findPostGroupInfoByUserId(userId, pageable);
	}

	@Override
	public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

		List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserId(currentUserId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("get list group join successfully!")
				.result(list).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

		List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserIdOfUser(currentUserId);
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("get list group post owner successfully!")
						.result(list).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getSuggestionPostGroupByUserId(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

		PageRequest pageable = PageRequest.of(0, 10);

		List<FriendRequestResponse> fList = friendRepository.findFriendTop10UserIdsByUserId(currentUserId, pageable);
		Set<GroupPostResponse> listGroupSuggest = new HashSet<>();
		for (FriendRequestResponse f : fList) {
			List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserIdOfUser(f.getUserId());
			listGroupSuggest.addAll(list);
		}
		if (!listGroupSuggest.isEmpty()) {
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("get list group post successfully!")
							.result(listGroupSuggest).statusCode(HttpStatus.OK.value()).build());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
				.message("Not found group post of this user").statusCode(HttpStatus.NOT_FOUND.value()).build());
	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> createPostGroupByUserId(PostGroupDTO postGroup, String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<PostGroup> group = postGroupRepository.findByPostGroupName(postGroup.getPostGroupName());
		if (group.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT) // Sử dụng HttpStatus.CONFLICT cho lỗi đã tồn tại
					.body(GenericResponse.builder().success(false).message("Lỗi đã tồn tại trong dữ liệu")
							.statusCode(HttpStatus.CONFLICT.value()).build());

		}
		Optional<User> user = userRepository.findById(currentUserId);

		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		Date date = new Date();
		PostGroup groupEntity = new PostGroup();
		groupEntity.setPostGroupName(postGroup.getPostGroupName());
		groupEntity.setIsPublic(postGroup.isPublic());
		groupEntity.setBio(postGroup.getBio());
		groupEntity.setIsApprovalRequired(postGroup.isApprovalRequired());
		groupEntity.setCreateDate(date);
		groupEntity.setUpdateDate(date);

		Optional<PostGroupMember> postGroupMember = groupMemberRepository
				.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);
		PostGroupMember postMember = new PostGroupMember();
		if (postGroupMember.isPresent()) {
			postMember = postGroupMember.get();
		} else {
			postMember.setRoleUserGroup(RoleUserGroup.Admin);
			postMember.setUser(user.get());
		}
		postMember.getPostGroup().add(groupEntity);

		Set<PostGroupMember> list = new HashSet<>();
		list.add(postMember);
		groupEntity.setPostGroupMembers(list);

		// Request Post Group
		if (postGroup.getUserId() != null) {
			for (String idRequest : postGroup.getUserId()) {
				Optional<User> userMember = userRepository.findById(idRequest);
				if (userMember.isPresent() && (!userMember.equals(user))) {
					PostGroupRequest postGroupRequest = new PostGroupRequest();
					postGroupRequest.setCreateDate(date);
					postGroupRequest.setInvitedUser(userMember.get());
					postGroupRequest.setInvitingUser(user.get());
					postGroupRequest.setPostGroup(groupEntity);
					postGroupRequest.setIsAccept(false);
					postGroupRequestRepository.save(postGroupRequest);

				}
			}
		}
		postGroupRepository.save(groupEntity);
		groupMemberRepository.save(postMember);
		return ResponseEntity.status(HttpStatus.CREATED) // Sử dụng HttpStatus.CREATED cho tạo thành công
				.body(GenericResponse.builder().success(true).message("Tạo thành công")
						.result(groupEntity.getPostGroupId())// Thông báo tạo thành công
						.statusCode(HttpStatus.CREATED.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup,
			String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		try {
			Date date = new Date();
			PostGroup entity = groupPost.get();
			entity.setPostGroupName(postGroup.getPostGroupName());
			entity.setIsPublic(postGroup.isPublic());
			entity.setIsApprovalRequired(postGroup.isApprovalRequired());
			entity.setBio(postGroup.getBio());
			entity.setUpdateDate(date);
			postGroupRepository.save(entity);
			return ResponseEntity.status(HttpStatus.OK) // Sử dụng HttpStatus.OK cho cập nhật thành công
					.body(GenericResponse.builder().success(true).message("Cập nhật thành công") // Thông báo cập nhật
																									// thành công
							.statusCode(HttpStatus.OK.value()).build());
		} catch (Exception e) {
			e.printStackTrace(); // In thông tin lỗi
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // HttpStatus.INTERNAL_SERVER_ERROR cho lỗi
																			// server
					.body(GenericResponse.builder().success(false).message("Lỗi khi cập nhật") // Thông báo lỗi
							.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).build());

		}

	}

	@Override
	public ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());

		PostGroup entity = groupPost.get();
		if (postGroup.getAvatar() != null) {
			String avatarOld = entity.getAvatarGroup();
			entity.setAvatarGroup(updateImage(avatarOld, postGroup.getAvatar()));
			postGroupRepository.save(entity);
		}
		if (postGroup.getBackground() != null) {
			String backgroundOld = entity.getBackgroundGroup();
			entity.setBackgroundGroup(updateImage(backgroundOld, postGroup.getBackground()));
			postGroupRepository.save(entity);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully")
				.result(postGroup).statusCode(HttpStatus.OK.value()).build());
	}

	public String updateImage(String oldImage, MultipartFile newImage) {
		String result = null;
		try {
			result = cloudinaryService.uploadImage(newImage);
			if (oldImage != null) {
				cloudinaryService.deleteImage(oldImage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ResponseEntity<GenericResponse> deletePostGroup(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		PostGroup entity = groupPost.get();
		try {
			Optional<PostGroupMember> groupMember = groupMemberRepository
					.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);
			if (groupMember.isPresent() && entity.getPostGroupMembers().contains(groupMember.get())) {
				groupMember.get().setPostGroup(null);
				entity.setPostGroupMembers(null);
				groupMemberRepository.save(groupMember.get());
				try {
					postGroupRepository.delete(entity);
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
							.body(GenericResponse.builder().success(false).message(e.getMessage())
									.statusCode(HttpStatus.EXPECTATION_FAILED.value()).build());
				}

			} else {
				return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // Sử dụng HttpStatus.NOT_ACCEPTABLE cho lỗi
																		// "NoAccept"
						.body(GenericResponse.builder().success(false).message("No Accept") // Thông báo lỗi "No Accept"
								.statusCode(HttpStatus.NOT_ACCEPTABLE.value()).build());

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete successfully")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> acceptPostGroup(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);

		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroupRequest> poOptional = postGroupRequestRepository
				.findByInvitedUserUserIdAndPostGroupPostGroupId(currentUserId, postId);

		if (poOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found Request").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		// TH Group kiem tra quyen tu dong vao nhom
		// TH: Group kiem tra thanh vien nhom
		if (Boolean.TRUE.equals(groupPost.get().getIsApprovalRequired())) {
			poOptional.get().setIsAccept(true);
			postGroupRequestRepository.save(poOptional.get());

			// Tu dong vao nhom
		} else {
			Optional<PostGroupMember> postGroupMember = groupMemberRepository
					.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Member);
			PostGroupMember postMember1 = new PostGroupMember();
			if (postGroupMember.isPresent()) {
				postMember1 = postGroupMember.get();
			} else {
				postMember1.setRoleUserGroup(RoleUserGroup.Member);
				postMember1.setUser(user.get());
			}
			postMember1.getPostGroup().add(groupPost.get());
			groupPost.get().getPostGroupMembers().add(postMember1);
			groupMemberRepository.save(postMember1);
			postGroupRepository.save(groupPost.get());
			postGroupRequestRepository.delete(poOptional.get());
		}

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully")
				.statusCode(HttpStatus.OK.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> declinePostGroup(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroupRequest> poOptional = postGroupRequestRepository
				.findByInvitedUserUserIdAndPostGroupPostGroupId(currentUserId, postId);
		if (poOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group request").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		postGroupRequestRepository.delete(poOptional.get());
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete successfully")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		// Lap qua mang UserId de gui loi moi vao nhom
		if (postGroup.getUserId() != null) {
			for (String idRequest : postGroup.getUserId()) {
				Optional<User> userMember = userRepository.findById(idRequest);
				if (userMember.isPresent() && (!userMember.equals(user))) {
					Date date = new Date();
					PostGroupRequest postGroupRequest = new PostGroupRequest();
					postGroupRequest.setCreateDate(date);
					postGroupRequest.setInvitedUser(userMember.get());
					postGroupRequest.setInvitingUser(user.get());
					postGroupRequest.setPostGroup(groupPost.get());
					postGroupRequest.setIsAccept(false);
					postGroupRequestRepository.save(postGroupRequest);
				}
			}
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Invite successfully")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> acceptMemberPostGroup(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroupMember> groupMember = groupMemberRepository.findByUserUserIdAndRoleUserGroup(currentUserId,
				RoleUserGroup.Admin);
		// Kiểm tra quyền của currentUser
		if (groupMember.isPresent() && groupPost.get().getPostGroupMembers().contains(groupMember.get())) {

			for (String userId : postGroup.getUserId()) {
				Optional<User> userMember = userRepository.findById(userId);

				// Kiểm tra user muốn chấp nhập có tồn tại không
				if (userMember.isPresent()) {
					Optional<PostGroupRequest> poOptional = postGroupRequestRepository
							.findByInvitedUserUserIdAndPostGroupPostGroupId(userId, postGroup.getPostGroupId());
					if (poOptional.isPresent()) {
						Optional<PostGroupMember> postGroupMember = groupMemberRepository
								.findByUserUserIdAndRoleUserGroup(userId, RoleUserGroup.Member);
						PostGroupMember postMember1 = new PostGroupMember();
						if (postGroupMember.isPresent()) {
							postMember1 = postGroupMember.get();
						} else {
							postMember1.setRoleUserGroup(RoleUserGroup.Member);
							postMember1.setUser(userMember.get());
						}
						postMember1.getPostGroup().add(groupPost.get());
						groupPost.get().getPostGroupMembers().add(postMember1);
						groupMemberRepository.save(postMember1);
						postGroupRepository.save(groupPost.get());
						postGroupRequestRepository.delete(poOptional.get());
					} else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
								.message("Not found request").statusCode(HttpStatus.NOT_FOUND.value()).build());
					}

				}
			}
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Invite successfully")
					.statusCode(HttpStatus.OK.value()).build());
		}
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // Sử dụng HttpStatus.NOT_ACCEPTABLE cho lỗi
				.body(GenericResponse.builder().success(false).message("No Accept") // Thông báo lỗi "No Accept"
						.statusCode(HttpStatus.NOT_ACCEPTABLE.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> getPostGroupInvitedByUserId(String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		List<InvitedPostGroupResponse> list = postGroupRepository.findPostGroupInvitedByUserId(currentUserId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully").result(list)
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, Integer postId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> postGroup = postGroupRepository.findById(postId);
		if (postGroup.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		PostGroupResponse response = new PostGroupResponse();
		response.setPostGroupId(postId);
		response.setPostGroupName(postGroup.get().getPostGroupName());
		response.setAvatar(postGroup.get().getAvatarGroup());
		response.setBackground(postGroup.get().getBackgroundGroup());
		response.setIsPublic(postGroup.get().getIsPublic());
		response.setIsApprovalRequired(postGroup.get().getIsApprovalRequired());
		response.setBio(postGroup.get().getBio());
		response.setCountMember(postGroup.get().getPostGroupMembers().size());
		response.setRoleGroup(checkUserInGroup(user.get(), postGroup.get()));
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully").result(response)
				.statusCode(HttpStatus.OK.value()).build());
	}

	public String checkUserInGroup(User user, PostGroup group) {
		Set<PostGroupMember> member = group.getPostGroupMembers();
		for (PostGroupMember postGroupMember : member) {
			if (postGroupMember.getUser().equals(user)) {
				if (postGroupMember.getRoleUserGroup().equals(RoleUserGroup.Admin)) {
					return "Admin";
				}
				return "Member";
			}
		}
		Optional<PostGroupRequest> postGroupRequest = postGroupRequestRepository
				.findByInvitedUserUserIdAndPostGroupPostGroupId(user.getUserId(), group.getPostGroupId());
		if (postGroupRequest.isPresent()) {
			if (Boolean.TRUE.equals(postGroupRequest.get().getIsAccept())) {
				return "Waiting Accept";
			}
			return "Accept Invited";
		}
		return "None";
	}

	@Override
	public ResponseEntity<GenericResponse> joinPostGroup(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		for (PostGroupMember member : groupPost.get().getPostGroupMembers()) {
			if (member.getUser().equals(user.get())) {
				return ResponseEntity.status(HttpStatus.CONFLICT) // Sử dụng HttpStatus.CONFLICT cho lỗi đã tồn tại
						.body(GenericResponse.builder().success(false).message("user đã là thành viên của nhóm")
								.statusCode(HttpStatus.CONFLICT.value()).build());
			}
		}
		if (Boolean.TRUE.equals(groupPost.get().getIsApprovalRequired())) {
			PostGroupRequest postGroupRequest = new PostGroupRequest();
			Date date = new Date();
			postGroupRequest.setCreateDate(date);
			postGroupRequest.setInvitingUser(user.get());
			postGroupRequest.setInvitedUser(user.get());
			postGroupRequest.setIsAccept(true);
			postGroupRequest.setPostGroup(groupPost.get());
			postGroupRequestRepository.save(postGroupRequest);
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully")
					.result("Waiting Accept").statusCode(HttpStatus.OK.value()).build());
		}
		Optional<PostGroupMember> member = groupMemberRepository.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Member);
		PostGroupMember postGroupMember = new PostGroupMember();
		if(member.isPresent()) {
			postGroupMember = member.get();
		}
			postGroupMember.setUser(user.get());
			postGroupMember.setRoleUserGroup(RoleUserGroup.Member);
			postGroupMember.getPostGroup().add(groupPost.get());
			groupPost.get().getPostGroupMembers().add(postGroupMember);
			groupMemberRepository.save(postGroupMember);
			postGroupRepository.save(groupPost.get());
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully")
					.result("Member").statusCode(HttpStatus.OK.value()).build());
	}

}
