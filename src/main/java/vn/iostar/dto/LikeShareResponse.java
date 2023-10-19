package vn.iostar.dto;



import lombok.Data;
import vn.iostar.entity.Like;

@Data
public class LikeShareResponse {
	
	private int likeId;
	private int shareId;
	private String userName;
	
	public LikeShareResponse(Like like) {
		super();
		this.likeId = like.getLikeId();
		this.shareId = like.getShare().getShareId();
		this.userName = like.getUser().getUserName();
	}

	public LikeShareResponse(int likeId, int shareId, String userName) {
		super();
		this.likeId = likeId;
		this.shareId = shareId;
		this.userName = userName;
	}

	
	
	
}
