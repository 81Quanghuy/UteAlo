package vn.iostar.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.contants.React;

@Data
public class ReactDTO {
	private React react;
	private String senderId;
	private String receiverId;
	private String content;
	private String messageId;
	private Date createAt;
	private Date updateAt;
}
