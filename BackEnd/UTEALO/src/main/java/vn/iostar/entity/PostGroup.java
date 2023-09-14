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
@Table(name = "POSTGROUP")
public class PostGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postGroupId;

	private String postGroupName;
	private Date createDate;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "postGroup_postGroupMember", joinColumns = @JoinColumn(name = "postGroupId"), inverseJoinColumns = @JoinColumn(name = "postGroupMemberId"))
	private List<PostGroupMember> postGroupMembers = new ArrayList<>();

	@OneToMany(mappedBy = "postGroup")
	private List<Post> posts;

}
