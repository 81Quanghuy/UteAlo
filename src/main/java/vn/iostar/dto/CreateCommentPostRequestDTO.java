package vn.iostar.dto;


import lombok.Data;

@Data
public class CreateCommentPostRequestDTO {
	private String content;
	private int postId;
}
