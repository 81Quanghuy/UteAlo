package vn.iostar.utealo;

import java.io.Serializable;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;	

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNTS")
@Builder
public class Account implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    private String email;
    
    private String password;
    
    @OneToOne
    @JoinColumn(name = "userId")
    private User user;
     
}
