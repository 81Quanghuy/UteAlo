package vn.iostar.dto;



import lombok.Data;

@Data
public class CreateLikePostRequestDTO {
	 private int commentId;
	 private String postId;
	 private String status;
	 private String userId;
}
