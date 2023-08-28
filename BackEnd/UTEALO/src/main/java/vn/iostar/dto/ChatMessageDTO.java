package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
	private int chatId;
    private String content;
    private Date createTime;
    private String messageType;
    private String contentType;
    private UserDTO userFrom;
    private ChatGroupDTO group;
}
