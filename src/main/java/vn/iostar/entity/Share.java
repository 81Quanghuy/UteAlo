package vn.iostar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHARES")
public class Share implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int shareId;
    
    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "postGroupId")
    private PostGroup postGroup;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    private Date createAt;

    private Date updateAt;

}

