package vn.iostar.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import vn.iostar.contants.PrivacyLevel;


@Data
public class CreatePostRequestDTO {
	private String location;
    private String content;
    private Date updateAt;
    private Date postTime;
    private MultipartFile photos;
    private MultipartFile files;
    private PrivacyLevel privacyLevel;
    private String userId;
    private int postGroupId;
}
