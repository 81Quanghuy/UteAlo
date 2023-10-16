package vn.iostar.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ReplyCommentShareRequestDTO {
	private String content;
	private MultipartFile photos;
	private int shareId;
	private int commentId;
}
