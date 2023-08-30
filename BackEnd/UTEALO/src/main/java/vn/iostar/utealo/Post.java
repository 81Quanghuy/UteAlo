package vn.iostar.utealo;

import java.io.Serializable;
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
@Table(name = "POSTS")
public class Post implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;
    
    private Date postTime;
    private String content;
    private String photos;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "postGroupId")
    private PostGroup postGroup;
    
    @OneToMany(mappedBy = "post")
	List<Like> likes;
    
    @OneToMany(mappedBy = "post")
	List<Comment> comments;
    
    
}

