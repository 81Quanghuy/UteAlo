package vn.iostar.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.RoleUserGroup;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.PostGroupMember;

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
	private Set<String> managerId;

	public PostGroupResponse(Integer postGroupId, String postGroupName, String bio, Integer countMember,
			String groupType, String userJoinStatus, String avatar, String background, String roleGroup) {
		this.postGroupId = postGroupId;
		this.postGroupName = postGroupName;
		this.bio = bio;
		this.countMember = countMember;
		this.groupType = groupType;
		this.userJoinStatus = userJoinStatus;
		this.avatar = avatar;
		this.background = background;
		this.roleGroup = roleGroup;
	}

	public PostGroupResponse(PostGroup group) {
		this.postGroupId = group.getPostGroupId();
		this.avatar = group.getAvatarGroup();
		this.postGroupName = group.getPostGroupName();
		this.background = group.getBackgroundGroup();
		this.groupType = Boolean.TRUE.equals(group.getIsPublic()) ? "Public" : "Private";
		this.userJoinStatus = Boolean.TRUE.equals(group.getIsApprovalRequired()) ? "denied" : "allowed";
		this.bio = group.getBio();
		this.countMember = group.getPostGroupMembers().size();
		Set<String> list = new HashSet<>();
		for (PostGroupMember member : group.getPostGroupMembers()) {
			if (member.getRoleUserGroup().equals(RoleUserGroup.Admin)
					|| member.getRoleUserGroup().equals(RoleUserGroup.Deputy)) {
				list.add(member.getUser().getUserId());
			}
		}
		this.managerId = list;
	}
}
