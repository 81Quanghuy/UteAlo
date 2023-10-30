package vn.iostar.dto;

import lombok.Data;

@Data
public class SharePostRequestDTO {
	private String content;
	private Integer postId;
	private Integer postGroupId;
}
