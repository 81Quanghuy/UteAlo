package vn.iostar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.DateEntity;
import vn.iostar.entity.Notification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO extends DateEntity {
	private String notificationId;
	private Integer postId;
	private Integer shareId;
	private Integer commentId;
	private Integer friendRequestId;
	private Integer groupId;
	private String userId;
	private String photo;
	private Boolean isAdmin;

	private String content;
	private String link;
	private Boolean isRead;

	public NotificationDTO(Notification entity) {
		this.notificationId = entity.getNotificationId();
		if (entity.getShare() != null) {
			this.shareId = entity.getShare().getShareId();
		}
		if (entity.getPost() != null) {
			this.postId = entity.getPost().getPostId();
		}
		if (entity.getFriendRequest() != null) {
			this.friendRequestId = entity.getFriendRequest().getFriendRequestId();
		}
		if (entity.getComment() != null) {
			this.commentId = entity.getComment().getCommentId();
		}
		if (entity.getPostGroup() != null) {
			this.groupId = entity.getPostGroup().getPostGroupId();
		}

		this.userId = entity.getUser().getUserId();
		this.photo = entity.getPhoto();
		this.content = entity.getContent();
		this.link = entity.getLink();
		this.isRead = entity.getIsRead();

	}
}
