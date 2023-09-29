package vn.iostar.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.RoleUserGroup;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "POSTGROUPMEMBER")
public class PostGroupMember implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postGroupMemberId;

	@ManyToMany(mappedBy = "postGroupMembers")
	private List<PostGroup> postGroup = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@Enumerated(EnumType.STRING)
	private RoleUserGroup roleUserGroup;

}
