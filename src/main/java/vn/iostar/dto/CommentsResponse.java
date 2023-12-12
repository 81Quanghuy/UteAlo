package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.entity.Comment;

@Data
public class CommentsResponse {
	private int commentId;
	private String content;
	private Date createTime;
	private String photos;
	private String userName;
	private int postId;
	private int shareId;
	private String userAvatar;
	private String userId;
	private List<Integer> likes;
	private List<Integer> comments;
	private String userOwner;

	public CommentsResponse(Comment comment) {
		super();
		this.commentId = comment.getCommentId();
		this.content = comment.getContent();
		this.createTime = comment.getCreateTime();
		this.photos = comment.getPhotos();
		this.userName = comment.getUser().getUserName();
		if (comment.getPost() != null) {
			this.postId = comment.getPost().getPostId();
		}
		this.userAvatar = comment.getUser().getProfile().getAvatar();
		this.userId = comment.getUser().getUserId();
		if (comment.getShare() != null) {
			this.shareId = comment.getShare().getShareId();
		}

	}

}
