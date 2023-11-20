package vn.iostar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "FRIEND_REQUESTS",uniqueConstraints = {@UniqueConstraint(columnNames = {"userTo", "userFrom"})})
public class FriendRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int friendRequestId;
    
    @ManyToOne
    @JoinColumn(name = "userFrom")
    private User userFrom;
    
    @ManyToOne
    @JoinColumn(name = "userTo")
    private User userTo;
    
    private boolean isActive;
    private Date createdAt;
}

