package vn.iostar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.Gender;

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
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	private Date dayOfBirth;
	private boolean isActive = true;
	
	@Column(nullable = true)
    private boolean isVerified = false;

	@ManyToOne
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
	private List<FriendRequest> friendRequests1;

	@OneToMany(mappedBy = "userTo")
	private List<FriendRequest> friendRequests2;

	@OneToMany(mappedBy = "userFrom")
	private List<Message> messages1;

	@OneToMany(mappedBy = "userTo")
	private List<Message> messages2;

	@OneToMany(mappedBy = "user")
	private List<Like> likes;

	@OneToMany(mappedBy = "user")
	private List<Comment> comments;

	@OneToMany(mappedBy = "user")
	private List<Post> posts;

	@OneToMany(mappedBy = "user")
	private List<PostGroupMember> postGroupMembers;

	@OneToMany(mappedBy = "user")
	private List<ChatGroupMember> chatGroupMembers;

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens;
	
	@OneToOne(mappedBy = "user")
	private VerificationToken verificationToken;
}
