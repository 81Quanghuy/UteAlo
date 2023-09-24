package vn.iostar.dto;

import java.util.Date;

import lombok.Data;


@Data
public class CreatePostRequestDTO {
	private String location;
    private String content;
    private String photos;
    private Date updateAt;
    private Date postTime;
    private String userId;
    private int postGroupId;
}
