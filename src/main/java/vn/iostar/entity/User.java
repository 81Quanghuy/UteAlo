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

	@Column(columnDefinition = "nvarchar(255)")
	private String userName;
	@Column(columnDefinition = "nvarchar(255)")
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

	@OneToMany(mappedBy = "user1")
	private List<Friend> friend1;

	@OneToMany(mappedBy = "user2")
	private List<Friend> friend2;

	@OneToMany(mappedBy = "userFrom", fetch = FetchType.LAZY)
	private List<FriendRequest> friendRequests1;

	@OneToMany(mappedBy = "userTo", fetch = FetchType.LAZY)
	private List<FriendRequest> friendRequests2;

	@OneToMany(mappedBy = "user")
	private List<Like> likes;

	@OneToMany(mappedBy = "user")
	private List<Comment> comments;

	@OneToMany(mappedBy = "user")
	private List<Post> posts;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<PostGroupMember> postGroupMembers;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<ChatGroupMember> chatGroupMembers;

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens;

	@OneToOne(mappedBy = "user")
	private VerificationToken verificationToken;

	@OneToMany(mappedBy = "user")
	private List<Share> share;

	@OneToMany(mappedBy = "invitedUser", fetch = FetchType.LAZY)
	private List<PostGroupRequest> postGroupRequests;

	@OneToMany(mappedBy = "invitingUser", fetch = FetchType.LAZY)
	private List<PostGroupRequest> postGroupRequests1;

}
