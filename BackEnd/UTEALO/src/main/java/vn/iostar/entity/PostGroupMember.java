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
@Table(name = "POSTGROUPMEMBER")
public class PostGroupMember implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postGroupMemberId;
    
    @ManyToOne
    @JoinColumn(name = "postGroupId")
    private PostGroup postGroup;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    private int role;
    
}

