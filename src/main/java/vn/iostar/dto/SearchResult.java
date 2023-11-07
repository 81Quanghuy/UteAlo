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
public class SearchResult {
	private Integer postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String checkUserInGroup;
	private String bio;
	private boolean isPublic;
	private int countMember;
	private int countFriendJoinnedGroup;
	private String userId;
	private String userName;
	private String bioUser;
	private String avatar;
	private String background;
}
