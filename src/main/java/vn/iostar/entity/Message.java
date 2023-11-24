package vn.iostar.entity;

import java.io.Serial;
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
@Table(name = "MESSAGES")
public class Message implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String messageId;

	@Column(columnDefinition = "nvarchar(255)")
	private String content;

	@ManyToOne
	@JoinColumn(name = "senderId")
	private User sender;

	@ManyToOne
	@JoinColumn(name = "receiverId")
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "groupId")
	private PostGroup group;

	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
	private List<FilesMedia> files;



	private Date createAt;
	private Date updateAt;


}
