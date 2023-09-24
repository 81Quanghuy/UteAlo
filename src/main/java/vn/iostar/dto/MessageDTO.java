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
public class MessageDTO {
	private int messageId;
    private String messageType;
    private String contentType;
    private Date createTime;
    private UserDTO userFrom;
    private UserDTO userTo;
}
