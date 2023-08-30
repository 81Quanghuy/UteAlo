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
@Table(name = "PROFILES")
public class Profile implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int profileId;
    
    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
    
    private String bio;
    private String avatar;
    private String background;
    
}
