package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.PostGroup;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostGroupResponse {
	private Integer postGroupId;
	private String postGroupName;
	private String bio;
	private Integer countMember;
	private String groupType;// true: private, false: public
	private String userJoinStatus;
	private String avatar;
	private String background;
	private String roleGroup;

	public PostGroupResponse(PostGroup group) {
		this.postGroupId = group.getPostGroupId();
		this.avatar = group.getAvatarGroup();
		this.postGroupName = group.getPostGroupName();
		this.background = group.getBackgroundGroup();
		this.groupType = Boolean.TRUE.equals(group.getIsPublic()) ? "Public" : "Private";
		this.userJoinStatus = Boolean.TRUE.equals(group.getIsApprovalRequired()) ? "denied" : "allowed";
		this.bio = group.getBio();
		this.countMember = group.getPostGroupMembers().size();
	}
}
