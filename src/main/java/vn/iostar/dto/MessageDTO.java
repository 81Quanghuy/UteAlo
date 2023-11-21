package vn.iostar.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.entity.Files;
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

    private List<MultipartFile> files;
    private List<Files> fileEntities;
    private String content;

    private String senderAvatar;

    private String senderName;

    private Date createdAt;
    private Date updatedAt;

    // create contractor by MessageEntity
    public MessageDTO(Message message) {
        this.senderId = message.getSender().getUserId();
        this.receiverId = message.getReceiver().getUserId();
        this.groupId = String.valueOf(message.getGroup().getPostGroupId());
        this.fileEntities = message.getFiles();
        this.content = message.getContent();
        this.senderAvatar = message.getSender().getProfile().getAvatar();
        this.senderName = message.getSender().getUserName();
        this.createdAt = message.getCreateAt();
        this.updatedAt = message.getUpdateAt();
    }
}
