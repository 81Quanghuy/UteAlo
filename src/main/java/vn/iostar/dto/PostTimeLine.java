package vn.iostar.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;

import java.util.Date;

@Data
@NoArgsConstructor
public class PostTimeLine {
    private int postId;
    private Date postTime;
    private String content;
    private String photos;
    private String files;
    private String location;
    private RoleName roleName;
    private String groupName;
    private PrivacyLevel privacyLevel;
    private String userName;
    private String userAvatar;
    private String groupAvatar;
    private Integer likes;
    private Integer comments;

    public PostTimeLine(int postId, Date postTime, String content, String photos, String files, String location, RoleName roleName, String groupName, PrivacyLevel privacyLevel, String userName, String userAvatar, String groupAvatar, Integer likes, Integer comments) {
        this.postId = postId;
        this.postTime = postTime;
        this.content = content;
        this.photos = photos;
        this.files = files;
        this.location = location;
        this.roleName = roleName;
        this.groupName = groupName;
        this.privacyLevel = privacyLevel;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.groupAvatar = groupAvatar;
        this.likes = likes;
        this.comments = comments;
    }

    public PostTimeLine(int postId, Date postTime, String content, String photos, String files, String location, RoleName roleName, String groupName, PrivacyLevel privacyLevel, String userName, String userAvatar, String groupAvatar) {
        this.postId = postId;
        this.postTime = postTime;
        this.content = content;
        this.photos = photos;
        this.files = files;
        this.location = location;
        this.roleName = roleName;
        this.groupName = groupName;
        this.privacyLevel = privacyLevel;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.groupAvatar = groupAvatar;
    }
}