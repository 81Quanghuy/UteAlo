package vn.iostar.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;

@Data
public class SharePostRequestDTO {
	private String shareId;
	private String content;
	private Integer postId;
	private Integer postGroupId;
	private PrivacyLevel privacyLevel;
	private Date createAt;
	private Date updateAt;
	private String userId;
	
}
