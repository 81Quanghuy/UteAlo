package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.Gender;
import vn.iostar.dto.UserDTO;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "USERS")
public class User implements Serializable {

	@Serial
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
	private Boolean isOnline = false;

	@Column(nullable = true)
	private boolean isVerified = false;

	@ManyToOne
	@JoinColumn(name = "roleId")
	private Role role;

	@OneToOne(mappedBy = "user")
	private Account account;

	@OneToOne(mappedBy = "user")
	private Profile profile;

	@OneToMany(mappedBy = "user1", fetch = FetchType.LAZY)
	private List<Friend> friend1;

	@OneToMany(mappedBy = "user2", fetch = FetchType.LAZY)
	private List<Friend> friend2;

	@OneToMany(mappedBy = "userFrom", fetch = FetchType.LAZY)
	private List<FriendRequest> friendRequests1;

	@OneToMany(mappedBy = "userTo", fetch = FetchType.LAZY)
	private List<FriendRequest> friendRequests2;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Like> likes;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Comment> comments;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Post> posts;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<PostGroupMember> postGroupMembers;

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> refreshTokens;

	@OneToOne(mappedBy = "user")
	private VerificationToken verificationToken;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Share> share;

	@OneToMany(mappedBy = "invitedUser", fetch = FetchType.LAZY)
	private List<PostGroupRequest> postGroupRequests;

	@OneToMany(mappedBy = "invitingUser", fetch = FetchType.LAZY)
	private List<PostGroupRequest> postGroupRequests1;

	@OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Notification> notifications;

	@OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
	private List<Message> messages;

	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
	private List<Message> messages1;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<ReactMessage> react;
	
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Report> reports;
}
