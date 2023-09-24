package vn.iostar.dto;

import lombok.Data;
import vn.iostar.entity.Friend;

@Data
public class FriendReponse {
	private int friendId;
	private String user1;
	private String user2;
	private String status;
	public FriendReponse(Friend friend) {
		super();
		this.friendId = friend.getFriendId();
		this.user1 = friend.getUser1().getUserId();
		this.user2 = friend.getUser2().getUserId();
		this.status = friend.getStatus();
	}
	
}
