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
@Table(name = "CHATMESSAGES")
public class ChatMessage implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chatId;
    
    private String content;
    private Date createTime;
    private String messageType;
    private String contentType;
    
    @ManyToOne
    @JoinColumn(name = "userFrom")
    private User userFrom;
    
    @ManyToOne
    @JoinColumn(name = "groupId")
    private ChatGroup chatGroup;
    

}

