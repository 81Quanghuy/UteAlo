package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.contants.RoleUserGroup;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberGroupResponse {
	private String userId;
	private String username;
	private String avatarUser;
	private String backgroundUser;
	private String groupName;
	private RoleUserGroup roleName;
	private Date createAt;

}
