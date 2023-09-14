package vn.iostar.entity;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.contants.RoleName;


@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ROLE")
public class Role implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;
	
	@Enumerated(EnumType.STRING)
    private RoleName roleName;
    
    @OneToMany(mappedBy = "role")
	private List<User> users;
    
}

