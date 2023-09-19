package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.entity.Comment;
import vn.iostar.entity.Like;
import vn.iostar.entity.Post;


@Data
public class PostResponse {
	private int postId;
	private Date postTime;
    private Date updateAt;
    private String content;
    private String photos;
    private String location;
    private String userName;
    private String postGroupName;
    private List<Comment> comments;
    private List<Like> likes;
    
	public PostResponse(Post post) {
		this.postId = post.getPostId();
		this.postTime = post.getPostTime();
		this.updateAt = post.getUpdateAt();
		this.content = post.getContent();
		this.photos = post.getPhotos();
		this.location = post.getLocation();
		this.userName = post.getUser().getUserName();
		this.postGroupName = post.getPostGroup().getPostGroupName();
		this.comments = post.getComments();
		this.likes = post.getLikes();
	}

	
    
    
}
