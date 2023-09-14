package vn.iostar.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;
import vn.iostar.contants.Gender;
import vn.iostar.entity.User;

@Data
public class UserProfileResponse {

	private String userId;
	private String phone;
	private String email;
	private String fullName;
	private String avatar;
	private String address;
	private Date dateOfBirth;
	private String about;
	private Gender gender;
	private boolean isActive;
	private Date createdAt;
	private Date updatedAt;
	private String role;
	private List<String> friends = new ArrayList<>();

	public UserProfileResponse(User user) {
		this.userId = user.getUserId();
        this.phone = user.getPhone();
        this.email = user.getAccount().getEmail();
        this.fullName = user.getUserName();
        this.avatar = user.getProfile().getAvatar();
        this.address = user.getAddress();
        this.dateOfBirth = user.getDayOfBirth();
        this.about = user.getProfile().getBio();
        this.gender = user.getGender();
        this.isActive = user.isActive();
        this.createdAt = user.getAccount().getCreatedAt();
        this.updatedAt = user.getAccount().getUpdatedAt();
        this.role = user.getRole().getRoleName();    
	}

}