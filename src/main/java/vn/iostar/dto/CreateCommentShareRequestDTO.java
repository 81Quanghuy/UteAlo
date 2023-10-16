package vn.iostar.dto;


import lombok.Data;

@Data
public class CreateCommentShareRequestDTO {
	private String content;
	private int shareId;
}
