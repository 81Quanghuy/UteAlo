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
public class FriendRequestDTO {
	private int friendRequestId;
	private UserDTO userFrom;
	private UserDTO userTo;
	private boolean isActive;
}
