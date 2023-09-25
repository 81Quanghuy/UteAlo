package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {
	private String userId;
	private String avatar;
	private String username;
}
