package vn.iostar.entity;

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
@Table(name = "MESSAGES")
public class Message implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;
    
    private String messageType;
    @Column(columnDefinition = "nvarchar(255)")
    private String content;
    private Date createTime;
    
    @ManyToOne
    @JoinColumn(name = "userFrom")
    private User userFrom;
    
    @ManyToOne
    @JoinColumn(name = "userTo")
    private User userTo;
    
}

