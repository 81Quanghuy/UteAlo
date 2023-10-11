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
@Table(name = "LIKES")
public class Like implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int likeId;
    
	@ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "shareId")
    private Share share;
    
    private String status;
    
    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;

}

