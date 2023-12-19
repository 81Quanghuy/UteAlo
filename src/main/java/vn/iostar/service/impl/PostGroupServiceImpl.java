package vn.iostar.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.iostar.contants.RoleName;
import vn.iostar.contants.RoleUserGroup;
import vn.iostar.dto.FriendResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.InvitedPostGroupResponse;
import vn.iostar.dto.MemberGroupResponse;
import vn.iostar.dto.PaginationInfo;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.dto.PostGroupResponse;
import vn.iostar.dto.PostsResponse;
import vn.iostar.dto.SearchPostGroup;
import vn.iostar.dto.SearchUser;
import vn.iostar.dto.UserInviteGroup;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.PostGroupMember;
import vn.iostar.entity.PostGroupRequest;
import vn.iostar.entity.User;
import vn.iostar.exception.wrapper.BadRequestException;
import vn.iostar.repository.FriendRepository;
import vn.iostar.repository.PostGroupMemberRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.PostGroupRequestRepository;
import vn.iostar.repository.PostRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.FriendService;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.UserService;

@Service
public class PostGroupServiceImpl implements PostGroupService {

	@Autowired
	PostGroupRepository postGroupRepository;

	@Autowired
	PostGroupMemberRepository postGroupMemberRepository;

	@Autowired
	FriendService friendService;

	@Autowired
	UserService userService;

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

	@Autowired
	PostRepository postRepository;

	@Override
	public <S extends PostGroup> S save(S entity) {
		return postGroupRepository.save(entity);
	}

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

		List<FriendResponse> fList = friendRepository.findFriendByUserId(currentUserId, pageable);
		Set<GroupPostResponse> listGroupSuggest = new HashSet<>();
		for (FriendResponse f : fList) {
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
		groupEntity.setIsPublic(postGroup.getIsPublic());
		groupEntity.setBio(postGroup.getBio());
		groupEntity.setIsApprovalRequired(postGroup.getIsApprovalRequired());
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
				if (userMember.isPresent() && (!userMember.get().getUserId().equals(user.get().getUserId()))) {
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
						.result(Map.of("postGroupId", groupEntity.getPostGroupId()))// Thông báo tạo thành công
						.statusCode(HttpStatus.CREATED.value()).build());

	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> createPostGroupByAdmin(PostGroupDTO postGroup, String authorizationHeader) {
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
		groupEntity.setIsPublic(postGroup.getIsPublic());
		groupEntity.setBio(postGroup.getBio());
		groupEntity.setIsApprovalRequired(postGroup.getIsApprovalRequired());
		groupEntity.setCreateDate(date);
		groupEntity.setUpdateDate(date);

		// Tạo người dùng là admin của nhóm
		PostGroupMember postMember = new PostGroupMember();
		postMember.setRoleUserGroup(RoleUserGroup.Admin);
		postMember.setUser(user.get());
		postMember.getPostGroup().add(groupEntity); // Thêm nhóm vào danh sách nhóm của người dùng
		groupEntity.getPostGroupMembers().add(postMember); // Thêm thành viên vào nhóm

		// Thêm các thành viên khác vào nhóm (nếu có)
		if (postGroup.getUserId() != null) {
			for (String idRequest : postGroup.getUserId()) {
				Optional<User> userMember = userRepository.findById(idRequest);
				if (userMember.isPresent() && (!userMember.get().getUserId().equals(user.get().getUserId()))) {
					PostGroupMember member = new PostGroupMember();
					member.setRoleUserGroup(RoleUserGroup.Member);
					member.setUser(userMember.get());
					member.getPostGroup().add(groupEntity);
					groupEntity.getPostGroupMembers().add(member);
				}
			}
		}

		// Lưu thông tin nhóm và thành viên vào cơ sở dữ liệu
		postGroupRepository.save(groupEntity);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(GenericResponse.builder().success(true).message("Tạo thành công")
						.result(Map.of("postGroupId", groupEntity.getPostGroupId()))
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
			entity.setIsPublic(postGroup.getIsPublic());
			entity.setIsApprovalRequired(postGroup.getIsApprovalRequired());
			entity.setBio(postGroup.getBio());
			entity.setUpdateDate(date);
			postGroupRepository.save(entity);
			return ResponseEntity.status(HttpStatus.OK) // Sử dụng HttpStatus.OK cho cập nhật thành công
					.body(GenericResponse.builder().success(true).message("Cập nhật thành công") // Thông báo cập nhật
							// thành công
							.statusCode(HttpStatus.OK.value()).build());
		} catch (Exception e) {
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
		} else if (postGroup.getBackground() != null) {
			String backgroundOld = entity.getBackgroundGroup();
			entity.setBackgroundGroup(updateImage(backgroundOld, postGroup.getBackground()));
			postGroupRepository.save(entity);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Upload successfully")
				.statusCode(HttpStatus.OK.value()).build());
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
			// Kiểm tra user đó có phải là admin không
			if (groupMember.isPresent() && entity.getPostGroupMembers().contains(groupMember.get())) {
				groupMember.get().setPostGroup(null);
				entity.setPostGroupMembers(null);
				groupMemberRepository.save(groupMember.get());
				postGroupRepository.delete(entity);

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
		// Tìm admin or debuty của nhóm
		boolean checkAdminOrDeputy = groupPost.get().getPostGroupMembers().stream()
				.anyMatch(memberGroup -> (memberGroup.getRoleUserGroup().equals(RoleUserGroup.Admin)
						|| memberGroup.getRoleUserGroup().equals(RoleUserGroup.Deputy))
						&& memberGroup.getUser().getUserId().equals(poOptional.get().getInvitingUser().getUserId()));

		// kiểm tra xét duyệt thành viên
		if (Boolean.TRUE.equals(groupPost.get().getIsApprovalRequired()) && !checkAdminOrDeputy) {

			poOptional.get().setIsAccept(true);
			poOptional.get().setInvitingUser(poOptional.get().getInvitedUser());
			postGroupRequestRepository.save(poOptional.get());
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully")
					.result("Waiting Accept").statusCode(HttpStatus.OK.value()).build());
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
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Accept successfully")
					.result("Member").statusCode(HttpStatus.OK.value()).build());
		}

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
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete successfully").result("None")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> invitePostGroup(PostGroupDTO postGroup, String currentUserId) {
		List<UserInviteGroup> nameUser = new ArrayList<>();
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

		boolean userInPostGroup = postGroupRepository.isUserInPostGroup(groupPost.get(), user.get());
		if (!userInPostGroup) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(GenericResponse.builder().success(false).message("User does not belong to the post group")
							.statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		// Lap qua mang UserId de gui loi moi vao nhom
		if (postGroup.getUserId() != null) {
			for (String idRequest : postGroup.getUserId()) {

				Optional<PostGroupRequest> requestGroup = postGroupRequestRepository
						.findByInvitedUserUserIdAndPostGroupPostGroupId(idRequest, postGroup.getPostGroupId());
				Optional<User> userMember = userRepository.findById(idRequest);

				// Kiểm tra người được mời có tồn tại không
				if (userMember.isPresent()) {
					// Kiểm tra người được mời có nằm trong nhóm đó hay chưa
					boolean checkInGroup = postGroupRepository.isUserInPostGroup(groupPost.get(), userMember.get());

					// Kiểm tra chưa mời, không mời chính mình và chưa có trong nhóm đó
					if (requestGroup.isEmpty() && (!userMember.equals(user)) && !checkInGroup) {
						Date date = new Date();
						PostGroupRequest postGroupRequest = new PostGroupRequest();
						postGroupRequest.setCreateDate(date);
						postGroupRequest.setInvitedUser(userMember.get());
						postGroupRequest.setInvitingUser(user.get());
						postGroupRequest.setPostGroup(groupPost.get());
						postGroupRequest.setIsAccept(false);
						postGroupRequestRepository.save(postGroupRequest);
					} else {
						nameUser.add(new UserInviteGroup(userMember.get().getUserId(), userMember.get().getUserName()));
					}
				}
			}
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Invite successfully").result(nameUser)
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
		Optional<PostGroupMember> deputyMember = groupMemberRepository.findByUserUserIdAndRoleUserGroup(currentUserId,
				RoleUserGroup.Deputy);
		// Kiểm tra quyền của currentUser
		if (groupMember.isPresent() && groupPost.get().getPostGroupMembers().contains(groupMember.get())
				|| (deputyMember.isPresent() && groupPost.get().getPostGroupMembers().contains(deputyMember.get()))) {

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
	public ResponseEntity<GenericResponse> getPostGroupRequestsSentByUserId(String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		List<InvitedPostGroupResponse> list = postGroupRepository.findPostGroupRequestsSentByUserId(currentUserId);
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

		PostGroupResponse response = new PostGroupResponse(postGroup.get());
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
				} else if (postGroupMember.getRoleUserGroup().equals(RoleUserGroup.Deputy)) {
					return "Deputy";
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
		Optional<PostGroupMember> member = groupMemberRepository.findByUserUserIdAndRoleUserGroup(currentUserId,
				RoleUserGroup.Member);
		PostGroupMember postGroupMember = new PostGroupMember();
		if (member.isPresent()) {
			postGroupMember = member.get();
		}
		postGroupMember.setUser(user.get());
		postGroupMember.setRoleUserGroup(RoleUserGroup.Member);
		postGroupMember.getPostGroup().add(groupPost.get());
		groupPost.get().getPostGroupMembers().add(postGroupMember);
		groupMemberRepository.save(postGroupMember);
		postGroupRepository.save(groupPost.get());
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully").result("Member")
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getMemberByPostId(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<MemberGroupResponse> meList = new ArrayList<>();

		for (PostGroupMember member : groupPost.get().getPostGroupMembers()) {
			MemberGroupResponse mResponse = new MemberGroupResponse();
			mResponse.setGroupName(groupPost.get().getPostGroupName());
			mResponse.setUserId(member.getUser().getUserId());
			mResponse.setUsername(member.getUser().getUserName());
			mResponse.setAvatarUser(member.getUser().getProfile().getAvatar());
			mResponse.setRoleName(member.getRoleUserGroup());
			meList.add(mResponse);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully").result(meList)
				.statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getMemberRequiredByPostId(Integer postId, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postId);
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<MemberGroupResponse> meList = new ArrayList<>();
		List<PostGroupRequest> postRequestList = postGroupRequestRepository.findByIsAcceptAndPostGroupPostGroupId(true,
				postId);
		for (PostGroupRequest postGroupRequest : postRequestList) {
			MemberGroupResponse mResponse = new MemberGroupResponse();
			mResponse.setGroupName(groupPost.get().getPostGroupName());
			mResponse.setUserId(postGroupRequest.getInvitedUser().getUserId());
			mResponse.setUsername(postGroupRequest.getInvitedUser().getUserName());
			mResponse.setAvatarUser(postGroupRequest.getInvitedUser().getProfile().getAvatar());
			mResponse.setBackgroundUser(postGroupRequest.getInvitedUser().getProfile().getBackground());
			mResponse.setCreateAt(postGroupRequest.getCreateDate());
			meList.add(mResponse);
		}
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully").result(meList)
				.statusCode(HttpStatus.OK.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> assignDeputyByUserIdAndGroupId(PostGroupDTO postGroup,
			String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<PostGroupMember> postGroupMember = groupMemberRepository
				.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);

		// user dang nhap phai la admin
		if (postGroupMember.isPresent() && groupPost.get().getPostGroupMembers().contains(postGroupMember.get())) {

			// Check user muon thanh admin ton tai khong
			String userIdToAdmin = postGroup.getUserId().stream().findFirst().orElse(null);

			// da truyen vao user de chi dinh lam admin
			if (userIdToAdmin != null && !userIdToAdmin.equals(currentUserId)) {
				Optional<User> userAdd = userRepository.findById(userIdToAdmin);
				if (userAdd.isEmpty())
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
							.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
				// cap de xoa di
				Optional<PostGroupMember> memberDelete = groupMemberRepository
						.findByUserUserIdAndRoleUserGroup(userIdToAdmin, RoleUserGroup.Member);
				if (memberDelete.isPresent()) {
					Optional<PostGroupMember> memberAddDeputy = groupMemberRepository
							.findByUserUserIdAndRoleUserGroup(userIdToAdmin, RoleUserGroup.Deputy);
					// Kiem tra user hien tai da co member la member chua
					if (memberAddDeputy.isPresent()) {
						memberAddDeputy.get().getPostGroup().add(groupPost.get());
						groupPost.get().getPostGroupMembers().add(memberAddDeputy.get());
						groupMemberRepository.save(memberAddDeputy.get());
					} else {
						PostGroupMember member = new PostGroupMember();
						member.setUser(userAdd.get());
						member.setRoleUserGroup(RoleUserGroup.Deputy);
						member.getPostGroup().add(groupPost.get());
						groupPost.get().getPostGroupMembers().add(member);
						groupMemberRepository.save(member);
					}
					groupPost.get().getPostGroupMembers().remove(memberDelete.get());
					postGroupRepository.save(groupPost.get());
					return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully")
							.statusCode(HttpStatus.OK.value()).build());
				}
			}
			// Khong ton tai user do group member
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group member").statusCode(HttpStatus.NOT_FOUND.value()).build());

		}
		// User dang dang nhap khong phai la admin
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // Sử dụng HttpStatus.NOT_ACCEPTABLE cho lỗi
				.body(GenericResponse.builder().success(false).message("No Accept") // Thông báo lỗi "No Accept"
						.statusCode(HttpStatus.NOT_ACCEPTABLE.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> deleteMemberByPostId(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		String userId = postGroup.getUserId().stream().findFirst().orElse(null);
		if (userId != null) {
			Optional<PostGroupMember> member = groupMemberRepository.findByUserUserIdAndRoleUserGroup(userId,
					RoleUserGroup.Member);
			if (member.isPresent() && member.get().getPostGroup().contains(groupPost.get())) {
				groupPost.get().getPostGroupMembers().remove(member.get());
				member.get().getPostGroup().remove(groupPost.get());
				postGroupRepository.save(groupPost.get());
				groupMemberRepository.save(member.get());

				return ResponseEntity.ok(GenericResponse.builder().success(true).message("Assign successfully")
						.statusCode(HttpStatus.OK.value()).build());
			} else {
				Optional<PostGroupMember> memberDeputy = groupMemberRepository.findByUserUserIdAndRoleUserGroup(userId,
						RoleUserGroup.Deputy);
				if (memberDeputy.isPresent() && memberDeputy.get().getPostGroup().contains(groupPost.get())) {
					groupPost.get().getPostGroupMembers().remove(memberDeputy.get());
					memberDeputy.get().getPostGroup().remove(groupPost.get());
					postGroupRepository.save(groupPost.get());
					groupMemberRepository.save(memberDeputy.get());

					return ResponseEntity.ok(GenericResponse.builder().success(true).message("Assign successfully")
							.statusCode(HttpStatus.OK.value()).build());
				}
			}
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found member").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
				.message("Not found user need delete").statusCode(HttpStatus.NOT_FOUND.value()).build());

	}

	@Override
	public ResponseEntity<GenericResponse> declineMemberRequiredByPostId(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		List<PostGroupRequest> requestList = postGroupRequestRepository.findByIsAcceptAndPostGroupPostGroupId(true,
				postGroup.getPostGroupId());
		List<User> listUser = new ArrayList<>();
		if (!requestList.isEmpty()) {
			for (String userId : postGroup.getUserId()) {
				Optional<User> userItem = userRepository.findById(userId);
				if (userItem.isEmpty())
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
							.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

				listUser.add(userItem.get());
			}

			for (User user2 : listUser) {
				for (PostGroupRequest requestItem : requestList) {
					if (requestItem.getInvitedUser().getUserId().equals(user2.getUserId())) {
						postGroupRequestRepository.delete(requestItem);
					}
				}
			}
			return ResponseEntity.ok(GenericResponse.builder().success(true).message("decline successfully")
					.statusCode(HttpStatus.OK.value()).build());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
				.message("Not found user need delete").statusCode(HttpStatus.NOT_FOUND.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userRepository.findById(currentUserId);

		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		PageRequest pageable = PageRequest.of(0, 20);
		List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserId(currentUserId, pageable);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("get list group join successfully!")
				.result(list).statusCode(HttpStatus.OK.value()).build());
	}

	public ResponseEntity<GenericResponse> leaveGroup(String userId, Integer groupId) {

		int hasAssociations = postGroupMemberRepository.hasPostGroupMemberAssociations(groupId, userId);
		if (hasAssociations == 1) {
			// Sử dụng phương thức deletePostGroupMemberAssociations để xóa mối quan hệ
			postGroupMemberRepository.deletePostGroupMemberAssociations(groupId, userId);

			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("get list group join successfully!")
							.result("None").statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
	}

	@Override
	public int getNumberOfFriendsInGroup(String userId, int postGroupId) {
		return postGroupMemberRepository.countFriendsInGroup(userId, postGroupId);
	}

	@Override
	public ResponseEntity<GenericResponse> assignAdminByUserIdAndGroupId(PostGroupDTO postGroup, String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<PostGroupMember> postGroupMember = groupMemberRepository
				.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);

		// user dang nhap phai la admin
		if (postGroupMember.isPresent() && groupPost.get().getPostGroupMembers().contains(postGroupMember.get())) {

			// Check user muon thanh admin ton tai khong
			String userIdToAdmin = postGroup.getUserId().stream().findFirst().orElse(null);

			// da truyen vao user de chi dinh lam admin
			if (userIdToAdmin != null && !userIdToAdmin.equals(currentUserId)) {
				Optional<User> userAdd = userRepository.findById(userIdToAdmin);
				// cap de xoa di
				Optional<PostGroupMember> memberAdmin = groupMemberRepository
						.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);

				if (memberAdmin.isPresent()) {
					// cap de them vao
					Optional<PostGroupMember> memberAdminAdd1 = groupMemberRepository
							.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Member);
					Optional<PostGroupMember> memberAdmin1 = groupMemberRepository
							.findByUserUserIdAndRoleUserGroup(userIdToAdmin, RoleUserGroup.Admin);

					// Kiem tra user hien tai da co member la member chua và thêm member đó vao nhóm
					checkMemberInGroup(groupPost.get(), user.get(), memberAdminAdd1);

					// Kiem tra user hien tai da co member la member chua
					if (memberAdmin1.isPresent()) {
						memberAdmin1.get().getPostGroup().add(groupPost.get());
						groupPost.get().getPostGroupMembers().add(memberAdmin1.get());
						groupMemberRepository.save(memberAdmin1.get());
					} else {
						PostGroupMember member = new PostGroupMember();
						member.setUser(userAdd.get());
						member.setRoleUserGroup(RoleUserGroup.Admin);
						member.getPostGroup().add(groupPost.get());
						groupPost.get().getPostGroupMembers().add(member);
						groupMemberRepository.save(member);
					}
					groupPost.get().getPostGroupMembers().remove(memberAdmin.get());
					// kiem tra neu user do co role la gi
					Optional<PostGroupMember> memberDeputy = groupMemberRepository
							.findByUserUserIdAndRoleUserGroup(userIdToAdmin, RoleUserGroup.Deputy);
					if (memberDeputy.isPresent()
							&& groupPost.get().getPostGroupMembers().contains(memberDeputy.get())) {
						groupPost.get().getPostGroupMembers().remove(memberDeputy.get());
						memberDeputy.get().getPostGroup().remove(groupPost.get());
						groupMemberRepository.save(memberDeputy.get());
					}
					// tuong tu voi role la member
					Optional<PostGroupMember> memberMember = groupMemberRepository
							.findByUserUserIdAndRoleUserGroup(userIdToAdmin, RoleUserGroup.Member);
					if (memberMember.isPresent()
							&& groupPost.get().getPostGroupMembers().contains(memberMember.get())) {
						groupPost.get().getPostGroupMembers().remove(memberMember.get());
						memberMember.get().getPostGroup().remove(groupPost.get());
						groupMemberRepository.save(memberMember.get());
					}

					postGroupRepository.save(groupPost.get());
					return ResponseEntity.ok(GenericResponse.builder().success(true).message("Join successfully")
							.statusCode(HttpStatus.OK.value()).build());
				}
			}
			// Khong ton tai user do group member
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group member").statusCode(HttpStatus.NOT_FOUND.value()).build());

		}
		// User dang dang nhap khong phai la admin
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // Sử dụng HttpStatus.NOT_ACCEPTABLE cho lỗi
				.body(GenericResponse.builder().success(false).message("No Accept") // Thông báo lỗi "No Accept"
						.statusCode(HttpStatus.NOT_ACCEPTABLE.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> removeDeputyByUserIdAndGroupId(PostGroupDTO postGroup,
			String currentUserId) {
		Optional<User> user = userRepository.findById(currentUserId);
		if (user.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());

		Optional<PostGroup> groupPost = postGroupRepository.findById(postGroup.getPostGroupId());
		if (groupPost.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		Optional<PostGroupMember> postGroupMember = groupMemberRepository
				.findByUserUserIdAndRoleUserGroup(currentUserId, RoleUserGroup.Admin);

		// user dang nhap phai la admin
		if (postGroupMember.isPresent() && groupPost.get().getPostGroupMembers().contains(postGroupMember.get())) {

			// Check user muon thanh admin ton tai khong
			String userIdRemove = postGroup.getUserId().stream().findFirst().orElse(null);

			// da truyen vao user de chi dinh lam admin
			if (userIdRemove != null && !userIdRemove.equals(currentUserId)) {
				Optional<User> userRemote = userRepository.findById(userIdRemove);
				if (userRemote.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
							.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
				}
				// cap de xoa di
				Optional<PostGroupMember> memberDeputyRemote = groupMemberRepository
						.findByUserUserIdAndRoleUserGroup(userIdRemove, RoleUserGroup.Member);
				checkMemberInGroup(groupPost.get(), userRemote.get(), memberDeputyRemote);
				Optional<PostGroupMember> memberRemove = groupMemberRepository
						.findByUserUserIdAndRoleUserGroup(userIdRemove, RoleUserGroup.Deputy);
				if (memberRemove.isEmpty())
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
							.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
				groupPost.get().getPostGroupMembers().remove(memberRemove.get());
				postGroupRepository.save(groupPost.get());
				postGroupRepository.save(groupPost.get());
				return ResponseEntity.ok(GenericResponse.builder().success(true).message("Remote successfully")
						.statusCode(HttpStatus.OK.value()).build());

			}
		}
		// User dang dang nhap khong phai la admin
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE) // Sử dụng HttpStatus.NOT_ACCEPTABLE cho lỗi
				.body(GenericResponse.builder().success(false).message("No Accept") // Thông báo lỗi "No Accept"
						.statusCode(HttpStatus.NOT_ACCEPTABLE.value()).build());
	}

	@Override
	public Optional<PostGroup> findByPostGroupName(String groupName) {
		return postGroupRepository.findByPostGroupName(groupName);
	}

	private void checkMemberInGroup(PostGroup groupPost, User userRemote,
			Optional<PostGroupMember> memberDeputyRemote) {
		if (memberDeputyRemote.isPresent()) {
			memberDeputyRemote.get().getPostGroup().add(groupPost);
			groupPost.getPostGroupMembers().add(memberDeputyRemote.get());
			groupMemberRepository.save(memberDeputyRemote.get());

		} else {
			PostGroupMember member = new PostGroupMember();
			member.setUser(userRemote);
			member.setRoleUserGroup(RoleUserGroup.Member);
			member.getPostGroup().add(groupPost);
			groupPost.getPostGroupMembers().add(member);
			groupMemberRepository.save(member);
		}
	}

	@Override
	public ResponseEntity<GenericResponse> findByPostGroupNameContainingIgnoreCase(String search, String userIdToken) {
		List<SearchPostGroup> list = postGroupRepository.findPostGroupNamesContainingIgnoreCase(search);
		Optional<User> user = userRepository.findById(userIdToken);
		List<SearchPostGroup> simplifiedGroupPosts = new ArrayList<>();
		// Lặp qua danh sách SearchPostGroup và thiết lập giá trị checkUserInGroup
		for (SearchPostGroup group : list) {
			Optional<PostGroup> postGroupOptional = findById(group.getPostGroupId());
			if (postGroupOptional.isPresent()) {
				String checkUser = checkUserInGroup(user.get(), postGroupOptional.get());
				System.out.println("HI");
				if (checkUser.equals("Admin") || checkUser.equals("Member")) {
					group.setCheckUserInGroup("isMember");
				} else {
					group.setCheckUserInGroup("isNotMember");
				}
				simplifiedGroupPosts.add(group);
			}
			group.setCountMember(postGroupOptional.get().getPostGroupMembers().size());
			group.setCountFriendJoinnedGroup(getNumberOfFriendsInGroup(userIdToken, group.getPostGroupId()));

		}

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully")
				.result(simplifiedGroupPosts).statusCode(HttpStatus.OK.value()).build());
	}

	@Override
	public ResponseEntity<GenericResponse> searchGroupAndUserContainingIgnoreCase(String search, String userIdToken) {
		Optional<User> user = userRepository.findById(userIdToken);
		Pageable pageable = PageRequest.of(0, 3);
		// List<SearchPostGroup> postGroups =
		// postGroupRepository.findTop3PostGroupNamesContainingIgnoreCase(search,pageable);
		List<SearchPostGroup> postGroups = postGroupRepository.findPostGroupNamesContainingIgnoreCase(search);
		List<SearchPostGroup> simplifiedGroupPosts = new ArrayList<>();
		// Lặp qua danh sách SearchPostGroup và thiết lập giá trị checkUserInGroup
		for (SearchPostGroup group : postGroups) {
			Optional<PostGroup> postGroupOptional = findById(group.getPostGroupId());
			if (postGroupOptional.isPresent()) {
				String checkUser = checkUserInGroup(user.get(), postGroupOptional.get());
				if (checkUser.equals("Admin") || checkUser.equals("Member")) {
					group.setCheckUserInGroup("isMember");
				} else {
					group.setCheckUserInGroup("isNotMember");
				}
				simplifiedGroupPosts.add(group);
			}
			group.setCountMember(postGroupOptional.get().getPostGroupMembers().size());
			group.setCountFriendJoinnedGroup(getNumberOfFriendsInGroup(userIdToken, group.getPostGroupId()));

		}
		List<SearchUser> users = postGroupRepository.findUsersByName(search);
		List<SearchUser> simplifiedUsers = new ArrayList<>();
		// Lặp qua danh sách SearchUser và thiết lập giá trị getStatusByUserId
		for (SearchUser userItem : users) {
			ResponseEntity<GenericResponse> check = friendService.getStatusByUserId(userItem.getUserId(), userIdToken);
			if (check.equals("Bạn bè")) {
				userItem.setCheckStatusFriend("isFriend");
			} else {
				userItem.setCheckStatusFriend("isNotFriend");
			}
			simplifiedUsers.add(userItem);
			Optional<User> userOptional = userService.findById(userItem.getUserId());
			if (userOptional.isPresent()) {
				userItem.setNumberFriend(userOptional.get().getFriend1().size());
				userItem.setAddress(userOptional.get().getAddress());
				userItem.setAvatar(userOptional.get().getProfile().getAvatar());
				userItem.setBackground(userOptional.get().getProfile().getBackground());
				userItem.setBio(userOptional.get().getProfile().getBio());
			}

		}
		List<Post> posts = postRepository.findByContentContaining(search, pageable);
		List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
		for (Post post : posts) {
			PostsResponse postsResponse = new PostsResponse(post, userIdToken);
			if (post.getComments() != null && !post.getComments().isEmpty()) {
				postsResponse.setComments(getIdComment(post.getComments()));
			} else {
				postsResponse.setComments(new ArrayList<>());
			}
			if (post.getLikes() != null && !post.getLikes().isEmpty()) {
				postsResponse.setLikes(getIdLikes(post.getLikes()));
			} else {
				postsResponse.setLikes(new ArrayList<>());
			}
			simplifiedUserPosts.add(postsResponse);
		}

		List<Object> combinedList = new ArrayList<>();
		combinedList.addAll(postGroups);
		combinedList.addAll(users);
		combinedList.addAll(simplifiedUserPosts);

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully")
				.result(combinedList).statusCode(HttpStatus.OK.value()).build());
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
	public Page<SearchPostGroup> findAllGroups(int page, int itemsPerPage) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
		Page<SearchPostGroup> postGroups = postGroupRepository.findAllPostGroups(pageable);

		Page<SearchPostGroup> simplifiedGroupPosts = postGroups.map(group -> {
			Optional<PostGroup> postGroupOptional = findById(group.getPostGroupId());
			if (postGroupOptional.isPresent()) {
				group.setCountMember(postGroupOptional.get().getPostGroupMembers().size());
			}
			return group;
		});
		return simplifiedGroupPosts;
	}

	@Override
	public ResponseEntity<GenericResponseAdmin> getAllGroups(String authorizationHeader, int page, int itemsPerPage) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(GenericResponseAdmin.builder().success(false)
					.message("No access").statusCode(HttpStatus.FORBIDDEN.value()).build());
		}

		Page<SearchPostGroup> groups = findAllGroups(page, itemsPerPage);
		long totalGroups = postGroupRepository.count();

		PaginationInfo pagination = new PaginationInfo();
		pagination.setPage(page);
		pagination.setItemsPerPage(itemsPerPage);
		pagination.setCount(totalGroups);
		pagination.setPages((int) Math.ceil((double) totalGroups / itemsPerPage));

		if (groups.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(true)
					.message("Empty").result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity.status(HttpStatus.OK)
					.body(GenericResponseAdmin.builder().success(true).message("Retrieved List of Groups Successfully")
							.result(groups).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
		}
	}

	@Override
	public ResponseEntity<GenericResponse> deletePostGroupByAdmin(Integer postGroupId, String authorizationHeader) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		Optional<User> user = userService.findById(currentUserId);
		RoleName roleName = user.get().getRole().getRoleName();
		if (!roleName.name().equals("Admin")) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroup> posOptional = findById(postGroupId);
		Page<SearchPostGroup> groups = findAllGroups(1, 10);
		if (posOptional.isPresent()) {
			PostGroup entity = posOptional.get();
			// Đặt giá trị của dữ liệu trong bảng PostGroupMember là null
			// Khi xóa thì chỉ xóa dữ liệu trong bảng PostGroup và PostGroup_PostGroupMember
			// thôi
			entity.setPostGroupMembers(null);
			save(entity);
			postGroupRepository.delete(entity);
			return ResponseEntity.ok()
					.body(new GenericResponse(true, "Delete Successful!", groups, HttpStatus.OK.value()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found post group!", null, HttpStatus.NOT_FOUND.value()));
		}

	}

	// Đếm số lượng nhóm từng tháng trong năm
	@Override
	public Map<String, Long> countGroupsByMonthInYear() {
		LocalDateTime now = LocalDateTime.now();
		int currentYear = now.getYear();

		// Tạo một danh sách các tháng
		List<Month> months = Arrays.asList(Month.values());
		Map<String, Long> groupCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

		for (Month month : months) {
			LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
			LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

			Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
			Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

			long grpupCount = postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
			groupCountsByMonth.put(month.toString(), grpupCount);
		}

		return groupCountsByMonth;
	}

	// Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
	public Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	// Chuyển sang giờ kết thức của 1 ngày là 23:59:59
	public Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}

	// Đếm số lượng user trong ngày hôm nay
	@Override
	public long countGroupsToday() {
		Date startDate = getStartOfDay(new Date());
		Date endDate = getEndOfDay(new Date());
		return postGroupRepository.countByCreateDateBetween(startDate, endDate);
	}

	// Đếm số lượng user trong 7 ngày
	@Override
	public long countGroupsInWeek() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
		Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		return postGroupRepository.countByCreateDateBetween(startDate, endDate);
	}

	// Đếm số lượng user trong 1 tháng
	@Override
	public long countGroupsInMonthFromNow() {
		// Lấy thời gian hiện tại
		LocalDateTime now = LocalDateTime.now();

		// Thời gian bắt đầu là thời điểm hiện tại trừ 1 tháng
		LocalDateTime startDate = now.minusMonths(1);

		// Thời gian kết thúc là thời điểm hiện tại
		LocalDateTime endDate = now;

		// Chuyển LocalDateTime sang Date (với ZoneId cụ thể, ở đây là
		// ZoneId.systemDefault())
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

		// Truy vấn số lượng user trong khoảng thời gian này
		return postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng user trong 3 tháng
	@Override
	public long countGroupsInThreeMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(3);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng user trong 6 tháng
	@Override
	public long countGroupsInSixMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(6);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng user trong 9 tháng
	@Override
	public long countGroupsInNineMonthsFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusMonths(9);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
	}

	// Đếm số lượng user trong 1 năm
	@Override
	public long countGroupsInOneYearFromNow() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startDate = now.minusYears(1);
		LocalDateTime endDate = now;
		Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
		Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return postGroupRepository.countByCreateDateBetween(startDateAsDate, endDateAsDate);
	}

	@Override
	public ResponseEntity<GenericResponse> cancelRequestPostGroup(Integer postGroupId, String currentUserId) {
		Optional<PostGroup> entity = postGroupRepository.findById(postGroupId);
		if (entity.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<PostGroupRequest> pOptional = postGroupRequestRepository.findRequestJoinGroup(currentUserId,
				currentUserId, postGroupId);
		if (pOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group post of this user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		postGroupRequestRepository.delete(pOptional.get());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete successfully").result("None")
				.statusCode(HttpStatus.OK.value()).build());
	}

	public ResponseEntity<GenericResponseAdmin> getPostGroupJoinByUserId(String userId, int page, int itemsPerPage) {
		Pageable pageable = PageRequest.of(page - 1, itemsPerPage); // Tính toán trang và số lượng phần tử trên trang

		Page<GroupPostResponse> groupPostPage = postGroupRepository.findPostGroupByUserId(userId, pageable);

		PaginationInfo pagination = new PaginationInfo();
		pagination.setPage(page);
		pagination.setItemsPerPage(itemsPerPage);
		pagination.setCount(groupPostPage.getTotalElements());
		pagination.setPages(groupPostPage.getTotalPages());

		if (groupPostPage.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
					.message("No Groups Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
		} else {
			return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
					.message("Retrieved List of Joined Groups Successfully").result(groupPostPage.getContent())
					.pagination(pagination).statusCode(HttpStatus.OK.value()).build());
		}
	}

	@Override
	public List<SearchPostGroup> getGroupsToday() {
		Date startDate = getStartOfDay(new Date());
		Date endDate = getEndOfDay(new Date());
		List<SearchPostGroup> groups = postGroupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
		return groups;
	}

	@Override
	public List<SearchPostGroup> getGroupsIn7Days() {
		Date startDate = getStartOfDay(getNDaysAgo(6));
		Date endDate = getEndOfDay(new Date());
		List<SearchPostGroup> groups = postGroupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
		return groups;
	}

	@Override
	public List<SearchPostGroup> getGroupsInMonth() {
		Date startDate = getStartOfDay(getNDaysAgo(30));
		Date endDate = getEndOfDay(new Date());
		List<SearchPostGroup> groups = postGroupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
		return groups;
	}

	public Date getNDaysAgo(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -days);
		return calendar.getTime();
	}

	// Thêm người dùng làm admin của nho
	@Override
	public ResponseEntity<GenericResponse> addAdminRoleInGroup(Integer groupId, String userId, String userIdToken) {
		Optional<User> userAmdin = userRepository.findById(userIdToken);
		if (userAmdin.isEmpty() || !userAmdin.get().getRole().getRoleName().equals(RoleName.Admin)) {
			throw new BadRequestException("Không thể thực hiện chức năng này!!!");
		}
		Optional<PostGroup> group = postGroupRepository.findById(groupId);
		if (group.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found group").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
					.message("Not found user").statusCode(HttpStatus.NOT_FOUND.value()).build());
		}

		PostGroupMember admin = group.get().getPostGroupMembers().stream()
				.filter(member -> member.getRoleUserGroup().equals(RoleUserGroup.Admin)).findFirst().orElse(null);
		if (admin != null) {
			return assignAdminByUserIdAndGroupId(new PostGroupDTO(groupId, userId), admin.getUser().getUserId());
		} else {

			// Check user do co trong group chua
			boolean check = true;
			Optional<PostGroupMember> adminMember = postGroupMemberRepository.findByUserUserIdAndRoleUserGroup(userId,
					RoleUserGroup.Admin);
			for (PostGroupMember postGroupMember : group.get().getPostGroupMembers()) {
				if (postGroupMember.getUser().getUserId().equals(userId)) {
					check = false;
					if (adminMember.isPresent()) {
						group.get().getPostGroupMembers().remove(postGroupMember);
						group.get().getPostGroupMembers().add(adminMember.get());
						adminMember.get().getPostGroup().add(group.get());
						groupMemberRepository.save(adminMember.get());

					} else {
						PostGroupMember postGroupMember2 = new PostGroupMember();
						postGroupMember2.setUser(user.get());
						postGroupMember2.setRoleUserGroup(RoleUserGroup.Admin);
						postGroupMember2.getPostGroup().add(group.get());
						group.get().getPostGroupMembers().add(postGroupMember2);
						groupMemberRepository.save(postGroupMember2);

					}
					break;
				}
			}
			if (check) {
				if (adminMember.isPresent()) {
					group.get().getPostGroupMembers().add(adminMember.get());
					adminMember.get().getPostGroup().add(group.get());
					groupMemberRepository.save(adminMember.get());
				} else {
					PostGroupMember postGroupMember3 = new PostGroupMember();
					postGroupMember3.setUser(user.get());
					postGroupMember3.setRoleUserGroup(RoleUserGroup.Admin);
					postGroupMember3.getPostGroup().add(group.get());
					group.get().getPostGroupMembers().add(postGroupMember3);
					groupMemberRepository.save(postGroupMember3);
				}
			}

			return ResponseEntity.ok(GenericResponse.builder().success(true).message("Add admin successfully")
					.statusCode(HttpStatus.OK.value()).build());
		}
	}

}
