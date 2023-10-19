package vn.iostar.dto;


import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateCommentShareRequestDTO {
	private String content;
	private MultipartFile photos;
	private int shareId;
}
