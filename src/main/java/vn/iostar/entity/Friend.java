package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

	@Serial
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId1")
    private User user1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId2")
    private User user2;
    
    private String status;

}

