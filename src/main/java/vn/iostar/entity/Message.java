package vn.iostar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.ChatStatus;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MESSAGES")
public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String messageId;
	
	
	private String messageType;
	@Column(columnDefinition = "nvarchar(255)")
	private String content;
	private Date createAt;
	private String senderId;
	private String senderName;
	private String receiverId;
	private String receiverName;
	private String groupId;
	private String groupIdName;
	@Enumerated(EnumType.STRING)
	private ChatStatus status;
}
