package vn.iostar.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.contants.Gender;
import vn.iostar.contants.RoleName;

@Data
public class UserResponse {

	private String userId;
	private String userName;
	private String address;
	private String phone;
	private Gender gender;
	private Date dayOfBirth;
	private String isActive;
	private RoleName roleName;
	private String email;

	public UserResponse(String userId, String userName, String address, String phone, Gender gender, Date dayOfBirth) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.address = address;
		this.phone = phone;
		this.gender = gender;
		this.dayOfBirth = dayOfBirth;
	}

}
