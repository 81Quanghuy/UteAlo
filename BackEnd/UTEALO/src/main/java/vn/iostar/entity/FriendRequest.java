package vn.iostar.utealo;

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
@Table(name = "FRIEND_REQUESTS")
public class FriendRequest {
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
    
}

