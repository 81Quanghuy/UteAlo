package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iostar.contants.React;
import vn.iostar.entity.Message;
import vn.iostar.entity.ReactMessage;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactDTO {
	private React react;
	private String senderId;
	private String receiverId;
	private String content;
	private Integer groupId;
	private String messageId;
	private Date createdAt;
	private Date updatedAt;
	private String reactUser;
	private String reactUserName;
	private Boolean isReact;

	public ReactDTO(ReactMessage entity, Message message) {
		if (entity.getReact() != null) {
			this.react = entity.getReact();
		}
		this.messageId = entity.getMessage().getMessageId();
		this.reactUser = entity.getUser().getUserId();
		this.reactUserName = entity.getUser().getUserName();
		if (message.getSender() != null) {
			this.senderId = message.getSender().getUserId();
		}
		if (message.getReceiver() != null) {
			this.receiverId = message.getReceiver().getUserId();
		}

		this.content = message.getContent();
		this.createdAt = message.getCreateAt();
		if (message.getGroup() != null) {
			this.groupId = message.getGroup().getPostGroupId();
		}

		this.updatedAt = entity.getCreateAt();
	}
}
