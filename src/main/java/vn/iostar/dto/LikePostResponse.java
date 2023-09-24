package vn.iostar.dto;



import lombok.Data;
import vn.iostar.entity.Like;

@Data
public class LikePostResponse {
	
	private int likeId;
	private int postId;
	private String userName;
	
	public LikePostResponse(Like like) {
		super();
		this.likeId = like.getLikeId();
		this.postId = like.getPost().getPostId();
		this.userName = like.getUser().getUserName();
	}

	public LikePostResponse(int likeId, int postId, String userName) {
		super();
		this.likeId = likeId;
		this.postId = postId;
		this.userName = userName;
	}

	
	
	
}
