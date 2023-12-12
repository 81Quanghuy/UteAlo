package vn.iostar.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.Message;
import vn.iostar.entity.ReactMessage;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
	private String messageId;
	private String senderId;
	private byte[] files;
	private String fileUrl;
	private Boolean isDeleted;

	private String receiverId;

	private String groupId;

	private String content;

	private String senderAvatar;

	private String senderName;

	private Date createdAt;
	private Date updatedAt;
	private List<ReactDTO> react;

	// create contractor by Message
	public MessageDTO(String senderId, String receiverId, Integer groupId, String files, String content,
			String senderAvatar, String senderName, Date createdAt, Boolean isDeleted) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.groupId = String.valueOf(groupId);
		this.fileUrl = files;
		this.content = content;
		this.senderAvatar = senderAvatar;
		this.senderName = senderName;
		this.createdAt = createdAt;
		this.isDeleted = isDeleted;
	}

	// create contractor by MessageEntity
	public MessageDTO(Message message) {
		this.senderId = message.getSender().getUserId();
		if (message.getReceiver() != null) {
			this.receiverId = message.getReceiver().getUserId();
		}
		if (message.getGroup() != null) {
			this.groupId = String.valueOf(message.getGroup().getPostGroupId());
		}
		this.fileUrl = message.getFiles();
		this.content = message.getContent();
		this.senderAvatar = message.getSender().getProfile().getAvatar();
		this.senderName = message.getSender().getUserName();
		this.createdAt = message.getCreateAt();
		this.updatedAt = message.getUpdateAt();
		this.messageId = message.getMessageId();
		this.isDeleted = message.getIsDeleted();
		List<ReactDTO> dto = new ArrayList<>();
		if (!message.getReact().isEmpty()) {

			for (ReactMessage reactEntity : message.getReact()) {
				dto.add(new ReactDTO(reactEntity, message));
			}

		}
		this.react = dto;
	}
}
