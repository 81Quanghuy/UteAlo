package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchPostGroup{
	private Integer postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String checkUserInGroup;
	private String bio;
	private Boolean isPublic;
	private int countMember;
	private int countFriendJoinnedGroup;
	
	public SearchPostGroup(Integer postGroupId, String postGroupName, String avatarGroup,String bio, Boolean isPublic) {
		super();
		this.postGroupId = postGroupId;
		this.postGroupName = postGroupName;
		this.avatarGroup = avatarGroup;
		this.bio = bio;
		this.isPublic = isPublic;
	}
	
	
	
}