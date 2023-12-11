package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.FriendRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendRequestDTO {
	private int friendRequestId;
	private String userFromId;
	private String userToId;
	private boolean isActive;

	public FriendRequestDTO(FriendRequest friendRequest) {
		this.friendRequestId = friendRequest.getFriendRequestId();
		this.isActive = friendRequest.isActive();
		this.userFromId = friendRequest.getUserFrom().getUserId();
		this.userToId = friendRequest.getUserTo().getUserId();
	}
}
