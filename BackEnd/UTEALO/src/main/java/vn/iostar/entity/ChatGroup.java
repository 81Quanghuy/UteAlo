package vn.iostar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CHATGROUP")
public class ChatGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int groupId;

	private String groupName;
	private Date createDate;

	@OneToMany(mappedBy = "chatGroup")
	private List<ChatMessage> chatMessages;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "chat_group_chat_group_member", joinColumns = @JoinColumn(name = "chat_group_id"), inverseJoinColumns = @JoinColumn(name = "chat_group_member_id"))
	private List<ChatGroupMember> chatGroupMembers = new ArrayList<>();

}
