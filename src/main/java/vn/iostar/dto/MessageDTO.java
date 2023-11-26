package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.Message;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
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

    //create contractor by Message
    public MessageDTO(String senderId, String receiverId, Integer groupId, String files, String content, String senderAvatar, String senderName, Date createdAt, Boolean isDeleted) {
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
    }
}
