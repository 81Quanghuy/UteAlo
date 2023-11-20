package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
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

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String messageId;
	
	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	@Column(columnDefinition = "nvarchar(255)")
	private String content;

	@ManyToOne
	@JoinColumn(name = "senderId")
	private User sender;

	@ManyToOne
	@JoinColumn(name = "receiverId")
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "groupId")
	private PostGroup group;

	@Enumerated(EnumType.STRING)
	private ChatStatus status;

	private Date createAt;
}
