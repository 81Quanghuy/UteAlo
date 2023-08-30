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
@Table(name = "FRIENDS")
public class Friend implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendId;
    
    @OneToOne
    @JoinColumn(name = "userId1")
    private User user1;
    
    @OneToOne
    @JoinColumn(name = "userId2")
    private User user2;
    
    private String status;

}

