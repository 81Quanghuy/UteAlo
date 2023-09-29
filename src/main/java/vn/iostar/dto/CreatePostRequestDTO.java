package vn.iostar.dto;

import java.util.Date;

import lombok.Data;


@Data
public class CreatePostRequestDTO {
	private String location;
    private String content;
    private Date updateAt;
    private Date postTime;
    private String photo;
    private String userId;
    private int postGroupId;
}
