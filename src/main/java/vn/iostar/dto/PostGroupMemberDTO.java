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
public class PostGroupMemberDTO {
	private int postGroupMemberId;
	private PostGroupDTO postGroup;
	private UserDTO user;
	private int role;
}
