package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.entity.Post;


@Data
public class PostResponse {
	private int postId;
	private Date postTime;
    private Date updateAt;
    private String content;
    private String photos;
    private String files;
    private String location;
    private String userId;
    private int postGroupId;
    private String postGroupName;
    private int comments;
    private int likes;
    
	public PostResponse(Post post) {
		this.postId = post.getPostId();
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdateAt();
		if(post.getContent()!=null) {
			this.content = post.getContent();
		}
		if(post.getPhotos()!=null) {
			this.photos = post.getPhotos();
		}
		if(post.getFiles()!=null) {
			this.files = post.getFiles();
		}
		if(post.getLocation()!=null) {
			this.location = post.getLocation();
		}
		this.userId = post.getUser().getUserId();
		if(post.getPostGroup() != null) {
			this.postGroupId = post.getPostGroup().getPostGroupId();
			this.postGroupName = post.getPostGroup().getPostGroupName();
		}
	}
}
