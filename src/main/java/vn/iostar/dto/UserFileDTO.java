package vn.iostar.dto;

import java.util.Date;

import lombok.Data;
import vn.iostar.contants.RoleName;
import vn.iostar.contants.RoleUserGroup;
import vn.iostar.entity.Account;

@Data
public class UserFileDTO {

	private Account account;
	private String userName;
	private String email;
	private String classUser;
	private String address;
	private Date dateOfBirth;
	private String gender;
	private Double phone;
	private RoleUserGroup roleUserGroup;
	private RoleName roleGroup;
}
