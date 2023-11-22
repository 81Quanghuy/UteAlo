package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.entity.Friend;
import vn.iostar.entity.FriendRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResponse {
    private String userId;
    private String background;
    private String avatar;
    private String username;
}
