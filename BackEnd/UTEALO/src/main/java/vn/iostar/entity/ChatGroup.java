package vn.iostar.entity;

import java.io.Serializable;
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
public class ChatGroup implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int groupId;
    
    private String groupName;
    private Date createDate;
    
    @OneToMany(mappedBy = "chatGroup")
    private List<ChatMessage> chatMessages;
    
    @ManyToMany
	@JoinTable(
			name = "chatGroup_chatGroupMember",
			joinColumns = @JoinColumn(name = "chatGroupId"),
			inverseJoinColumns = @JoinColumn(name = "memberId")
	)
    private List<ChatGroupMember> chatGroupMembers;
    
    
}

