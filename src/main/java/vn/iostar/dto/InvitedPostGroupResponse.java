package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitedPostGroupResponse {
	private String avatarGroup;
	private String backgroundGroup;
	private String bio;
	private String postGroupName;
	private String userName;
	private String avatarUser;

}
