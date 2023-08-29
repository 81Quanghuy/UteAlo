package vn.iostar.utealo;

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
@Table(name = "USERS")
public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    
    private String userName;
    private String address;
    private String phone;
    private String gender;
    private Date dayOfBirth;
    private boolean status;
    
    @OneToOne
    @JoinColumn(name = "roleId")
    private Role role;
    
    @OneToOne(mappedBy = "user")
	private Account account;
    
    @OneToOne(mappedBy = "user")
	private Profile profile;
    
    @OneToOne(mappedBy = "user1")
	private Friend friend;
    
    @OneToMany(mappedBy = "userFrom")
	List<FriendRequest> friendRequests;
    
    @OneToMany(mappedBy = "userFrom")
	List<Message> messages;
    
    @OneToMany(mappedBy = "user")
	List<Like> likes;
    
    @OneToMany(mappedBy = "user")
	List<Comment> comments;
    
    @OneToMany(mappedBy = "user")
	List<Post> posts;
    
    @OneToMany(mappedBy = "user")
	List<PostGroupMember> postGroupMembers;
    
    @OneToMany(mappedBy = "user")
	List<ChatGroupMember> chatGroupMembers;
    
}
