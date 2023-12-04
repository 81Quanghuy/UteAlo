package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Share;

@Data
public class SharesResponse {

    private int shareId;
    private String content;
    private Date createAt;
    private Date updateAt;
    private RoleName roleName;
    private String userId;
    private String userName;
    private String avatarUser;
    private PrivacyLevel privacyLevel;
    private PostsResponse postsResponse;
    private Integer postGroupId;
    private String postGroupName;
    private List<Integer> comments;
    private List<Integer> likes;

    public SharesResponse(Share share, String userId) {
        this.shareId = share.getShareId();
        this.content = share.getContent();
        this.createAt = share.getCreateAt();
        this.updateAt = share.getUpdateAt();
        this.postsResponse = new PostsResponse(share.getPost(), userId);
        this.userId = share.getUser().getUserId();
        this.avatarUser = share.getUser().getProfile().getAvatar();
        this.roleName = share.getUser().getRole().getRoleName();
        this.userName = share.getUser().getUserName();
        this.privacyLevel = share.getPrivacyLevel();
        if (share.getPostGroup() != null) {
            this.postGroupId = share.getPostGroup().getPostGroupId();
            this.postGroupName = share.getPostGroup().getPostGroupName();
        }

    }

    public SharesResponse(Share share) {
        this.shareId = share.getShareId();
        this.content = share.getContent();
        this.createAt = share.getCreateAt();
        this.updateAt = share.getUpdateAt();
        this.postsResponse = new PostsResponse(share.getPost());
        this.userId = share.getUser().getUserId();
        this.avatarUser = share.getUser().getProfile().getAvatar();
        this.roleName = share.getUser().getRole().getRoleName();
        this.userName = share.getUser().getUserName();
        this.privacyLevel = share.getPrivacyLevel();
        if (share.getPostGroup() != null) {
            this.postGroupId = share.getPostGroup().getPostGroupId();
            this.postGroupName = share.getPostGroup().getPostGroupName();
        }

    }
}
