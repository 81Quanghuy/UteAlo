package vn.iostar.dto;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;


@Data
public class CreatePostRequestDTO {
	private String location;
    private String content;
    private Date updateAt;
    private Date postTime;
    private MultipartFile photos;
    private String userId;
    private int postGroupId;
}
