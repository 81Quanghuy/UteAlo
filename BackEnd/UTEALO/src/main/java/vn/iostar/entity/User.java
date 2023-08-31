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
@Table(name = "USERS")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String userId;

	private String userName;
	private String address;
	private String phone;
	private String gender;
	private Date dayOfBirth;
	private boolean isActive = true;

	@OneToOne
	@JoinColumn(name = "roleId")
	private Role role;

	@OneToOne(mappedBy = "user")
	private Account account;

	@OneToOne(mappedBy = "user")
	private Profile profile;

	@OneToOne(mappedBy = "user1")
	private Friend friend1;

	@OneToOne(mappedBy = "user2")
	private Friend friend2;

	@OneToMany(mappedBy = "userFrom")
	List<FriendRequest> friendRequests1;

	@OneToMany(mappedBy = "userTo")
	List<FriendRequest> friendRequests2;

	@OneToMany(mappedBy = "userFrom")
	List<Message> messages1;

	@OneToMany(mappedBy = "userTo")
	List<Message> messages2;

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

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens;
}
