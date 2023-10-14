package vn.iostar.entity;

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
@Table(name = "COMMENTS")
public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;
    
	@ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    @Column(columnDefinition = "nvarchar(255)")
    private String content;
    private Date createTime;
    private Date updateAt;
    private String photos;
    
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "shareId")
    private Share share;
    
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;
    
    @OneToMany(mappedBy = "commentReply", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Comment> subComments;
    
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "comment_reply_id")
	private Comment commentReply;
}
