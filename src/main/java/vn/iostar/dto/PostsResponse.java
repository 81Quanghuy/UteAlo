package vn.iostar.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Post;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.User;

@Data
public class PostsResponse {
	private int postId;
	private Date postTime;
	private Date updateAt;
	private String content;
	private String photos;
	private String files;
	private String location;
	private String userId;
	private String userName;
	private String avatarUser;
	private int postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String groupType;
	private List<Integer> comments;
	private List<Integer> likes;
	private RoleName roleName;
	private PrivacyLevel privacyLevel;

	public PostsResponse(Post post, String userId) {
		initializeUserFields(post);
		initializePostFields(post, userId);
	}

	public PostsResponse(Post post) {
		initializeUserFields(post);
		initializePostFields(post);
	}

	private void initializeUserFields(Post post) {
		User user = post.getUser();
		this.userId = user.getUserId();
		this.avatarUser = Optional.ofNullable(user.getProfile()).map(profile -> profile.getAvatar()).orElse(null);
		this.userName = user.getUserName();
		this.roleName = user.getRole().getRoleName();
	}

	private void initializePostFields(Post post) {
		this.privacyLevel = post.getPrivacyLevel();
		this.postId = post.getPostId();
		this.content = post.getContent();
		this.files = post.getFiles();
		this.photos = post.getPhotos();
		this.location = post.getLocation();

		if (post.getPostGroup() != null) {
			PostGroup postGroup = post.getPostGroup();
			this.postGroupId = postGroup.getPostGroupId();
			this.postGroupName = postGroup.getPostGroupName();
			this.avatarGroup = postGroup.getAvatarGroup();
			this.groupType = Boolean.TRUE.equals(postGroup.getIsPublic()) ? "Public" : "Private";
		}
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdateAt();
	}

	private void initializePostFields(Post post, String userId) {
		this.privacyLevel = post.getPrivacyLevel();
		this.postId = post.getPostId();
		this.content = post.getContent();
		this.files = post.getFiles();
		this.photos = post.getPhotos();
		this.location = post.getLocation();

		initializeGroupFields(post, userId);

		if (post.getPrivacyLevel() == PrivacyLevel.PRIVATE && !post.getUser().getUserId().equals(userId)) {
			this.content = "Bài viết này được đăng với chế độ riêng tư!";
			this.photos = null;
			this.files = null;
			this.location = null;
		}

		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdateAt();
	}

	private void initializeGroupFields(Post post, String userId) {
		if (post.getPostGroup() != null) {
			PostGroup postGroup = post.getPostGroup();
			this.postGroupId = postGroup.getPostGroupId();
			this.postGroupName = postGroup.getPostGroupName();
			this.avatarGroup = postGroup.getAvatarGroup();
			this.groupType = Boolean.TRUE.equals(postGroup.getIsPublic()) ? "Public" : "Private";

			if (Boolean.FALSE.equals(postGroup.getIsPublic())) {
				Boolean checkMember = postGroup.getPostGroupMembers().stream()
						.anyMatch(member -> member.getUser().getUserId().equals(userId));

				if (Boolean.FALSE.equals(checkMember)) {
					this.content = "Bạn chưa tham gia nhóm nên không thể xem nội dung bài viết";
					this.photos = null;
					this.files = null;
					this.location = null;
				}
			}
		}
	}
}
