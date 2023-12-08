package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.entity.Comment;

@Data
public class CommentPostResponse {
	private int commentId;
	private String content;
	private Date createTime;
	private String photos;
	private String userName;
	private int postId;
	private String userAvatar;
	private String userId;
    private List<Integer> likes;
    private List<Integer> comments;
    private String userOwner;
	
	
	public CommentPostResponse(Comment comment) {
		super();
		this.commentId = comment.getCommentId();
		this.content = comment.getContent();
		this.createTime = comment.getCreateTime();
		this.photos = comment.getPhotos();
		this.userName = comment.getUser().getUserName();
		this.postId = comment.getPost().getPostId();
		this.userAvatar = comment.getUser().getProfile().getAvatar();
		this.userId = comment.getUser().getUserId();
	}

	public CommentPostResponse(int commentId, String content, Date createTime, String photos, String userName,
			int postId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.postId = postId;
	}

	public CommentPostResponse(int commentId, String content, Date createTime, String photos, String userName,
			int postId, String userAvatar, String userId) {
		super();
		this.commentId = commentId;
		this.content = content;
		this.createTime = createTime;
		this.photos = photos;
		this.userName = userName;
		this.postId = postId;
		this.userAvatar = userAvatar;
		this.userId = userId;
	}
	
}
