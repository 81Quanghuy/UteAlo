package vn.iostar.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNTS")
public class Account implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String accountId;

	@Email
	private String email;

	private String phone;

	@JsonBackReference
	private String password;

	@OneToOne
	@JoinColumn(name = "userId")
	private User user;

	private boolean isActive = true;
	private boolean isVerified = false;
	private Date createdAt;
	private Date updatedAt;
	private Date lastLoginAt;

}
