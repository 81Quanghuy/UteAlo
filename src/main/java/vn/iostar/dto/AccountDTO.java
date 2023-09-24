package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class AccountDTO {
	private String accountId;
	private String email;
	private String phone;
	private String password;
	private UserDTO user;
	@Builder.Default
	private Boolean isActive = true;

	private Date createdAt;

	private Date updatedAt;

	private Date lastLoginAt;
	@Builder.Default
	private Boolean isVerified = false;
}
