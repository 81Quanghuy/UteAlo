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
public class PostGroupResponse {
	private Integer postGroupId;
	private String postGroupName;
	private String bio;
	private Integer countMember;
	private Boolean isPublic;// true: private, false: public
	private Boolean isApprovalRequired;
	private String avatar;
	private String background;
	private String RoleGroup;
}
