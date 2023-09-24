package vn.iostar.dto;

import lombok.Data;
import vn.iostar.entity.Like;

@Data
public class LikeCommentResponse {
	private int likeId;
	private int commentId;
	private String userName;
	
	public LikeCommentResponse(int likeId, int commentId, String userName) {
		super();
		this.likeId = likeId;
		this.commentId = commentId;
		this.userName = userName;
	}
	
	public LikeCommentResponse(Like like) {
		super();
		this.likeId = like.getLikeId();
		this.commentId = like.getComment().getCommentId();
		this.userName = like.getUser().getUserName();
	}
	
	
}
