package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.FilesMedia;
import vn.iostar.entity.Message;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private String senderId;

    private String receiverId;

    private String groupId;

    private List<FilesMedia> fileEntities;

    private String content;

    private String senderAvatar;

    private String senderName;

    private Date createdAt;
    private Date updatedAt;

    // create contractor by MessageEntity
    public MessageDTO(Message message) {
        this.senderId = message.getSender().getUserId();
        if (message.getReceiver()!= null) {
            this.receiverId = message.getReceiver().getUserId();
        }
        if (message.getGroup() != null){
            this.groupId = String.valueOf(message.getGroup().getPostGroupId());
        }
        this.fileEntities = message.getFiles();
        this.content = message.getContent();
        this.senderAvatar = message.getSender().getProfile().getAvatar();
        this.senderName = message.getSender().getUserName();
        this.createdAt = message.getCreateAt();
        this.updatedAt = message.getUpdateAt();
    }
}
