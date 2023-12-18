package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postGroupMemberId;

	@ManyToMany(mappedBy = "postGroupMembers")
	@Cascade(value = { CascadeType.REMOVE })
	private List<PostGroup> postGroup = new ArrayList<>();

	@ManyToOne(cascade = jakarta.persistence.CascadeType.ALL)
	@JoinColumn(name = "userId")
	private User user;

	@Enumerated(EnumType.STRING)
	private RoleUserGroup roleUserGroup;

}
