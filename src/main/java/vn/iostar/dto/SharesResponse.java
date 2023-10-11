package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.entity.Share;


@Data
public class SharesResponse {

	private int shareId;
	private String content;
	private Date createAt;
	private Date updateAt;
	private int postId;
	private String userId;
	private List<Integer> comments;
	private List<Integer> likes;
	
	public SharesResponse(Share share) {
		this.shareId = share.getShareId();
		this.content = share.getContent();
		this.createAt = share.getCreateAt();
		this.updateAt = share.getUpdateAt();
		this.postId = share.getPost().getPostId();
		this.userId = share.getUser().getUserId();

	}
}
