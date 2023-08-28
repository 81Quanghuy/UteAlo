package vn.iostar.entity;

import java.io.Serializable;

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
@Table(name = "CHATGROUPMEMBER")
public class ChatGroupMember implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int memberId;
    
    @ManyToOne
    @JoinColumn(name = "groupId")
    private ChatGroup group;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    private String role;
    
    // Constructors, getters, setters, etc.
}

