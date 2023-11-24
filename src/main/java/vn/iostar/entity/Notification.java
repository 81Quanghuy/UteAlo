package vn.iostar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NOTIFICATIONS")
public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String notificationId;

    @Column(columnDefinition = "nvarchar(255)")
    private String content;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String link;
    private Date createAt;
    private String status;

}
