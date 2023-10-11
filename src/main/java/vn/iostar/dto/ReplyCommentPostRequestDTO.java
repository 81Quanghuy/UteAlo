package vn.iostar.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReplyCommentPostRequestDTO {
	private String content;
	private MultipartFile photos;
	private int postId;
	private int commentId;
}
