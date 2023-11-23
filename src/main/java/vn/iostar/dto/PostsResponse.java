package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Post;


@Data
public class PostsResponse {
	private int postId;
	private Date postTime;
    private Date updateAt;
    private String content;
    private String photos;
    private String files;
    private String location;
    private String userId;
    private String userName;
    private int postGroupId;
    private String postGroupName;
    private List<Integer> comments;
    private List<Integer> likes;
    private RoleName roleName;
    private PrivacyLevel privacyLevel;
    
    
	public PostsResponse(Post post) {
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
		if(post.getUser().getUserName() != null) {
			this.userName = post.getUser().getUserName();
		}
		if(post.getPostGroup() != null) {
			this.postGroupId = post.getPostGroup().getPostGroupId();
			this.postGroupName = post.getPostGroup().getPostGroupName();
		}
		if(post.getUser().getRole().getRoleName()!=null) {
			this.roleName = post.getUser().getRole().getRoleName();
		}
		if(post.getPrivacyLevel()!=null) {
			this.privacyLevel = post.getPrivacyLevel(); 
		}
	}
}
