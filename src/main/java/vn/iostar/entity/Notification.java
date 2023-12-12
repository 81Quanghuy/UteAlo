package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "NOTIFICATIONS")
public class Notification extends DateEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationId;

    private String link;
    @Column(columnDefinition = "nvarchar(255)")
    private String content;
    private String photo;
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "friendRequestId")
    private FriendRequest friendRequest;

    @ManyToOne
    @JoinColumn(name = "groupId")
    private PostGroup postGroup;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "shareId")
    private Share share;
    

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;
        
}
