package vn.iostar.utealo;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "COMMENTS")
public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;
    
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    
    private String content;
    private Date createTime;
    private String photos;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
}
