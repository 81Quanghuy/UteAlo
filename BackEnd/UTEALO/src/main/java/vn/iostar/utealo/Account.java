package vn.iostar.utealo;

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
@Table(name = "ACCOUNTS")
public class Account implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    private String email;
    
    private String password;
    
    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
     
}