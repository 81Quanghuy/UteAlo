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
import vn.iostar.contants.MessageType;

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
	
	@Enumerated(EnumType.STRING)
	private MessageType messageType;
	@Column(columnDefinition = "nvarchar(255)")
	private String content;
	private Date createAt;
	private String senderId;
	private String receiverId;
	private String groupId;

	@Enumerated(EnumType.STRING)
	private ChatStatus status;
}
