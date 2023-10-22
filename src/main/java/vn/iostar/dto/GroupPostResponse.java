package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.contants.RoleUserGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostResponse {
	private Integer postGroupId;
	private String postGroupName;
	private String avatarGroup;
	private String backgroundGroup;
	private RoleUserGroup role;	
}
