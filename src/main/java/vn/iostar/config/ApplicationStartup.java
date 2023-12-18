package vn.iostar.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import vn.iostar.contants.RoleName;
import vn.iostar.entity.Account;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.Profile;
import vn.iostar.entity.Role;
import vn.iostar.entity.User;
import vn.iostar.service.AccountService;
import vn.iostar.service.PostGroupService;
import vn.iostar.service.ProfileService;
import vn.iostar.service.RoleService;
import vn.iostar.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	String[] groupArray = { "201101C", "201102C", "201103C", "Công nghệ thông tin 1", "Công nghệ thông tin 2",
			"Công nghệ thông tin 3", "Công nghệ thông tin 4", "Xây dựng 1", "Xây dựng 2", "Xây dựng 3", "Xây dựng 4",
			"Kinh tế 1", "Kinh tế 2", "Kinh tế 3", "Kinh tế 4", "Du lịch 1", "Du lịch 2", "Du lịch 3", "Du lịch 4",
			"Ngoại ngữ 1", "Ngoại ngữ 2", "Ngoại ngữ 3", "Ngoại ngữ 4", "Ngân hàng 1", "Ngân hàng 2", "Ngân hàng 3",
			"Ngân hàng 4", "Quản trị kinh doanh 1", "Quản trị kinh doanh 2", "Quản trị kinh doanh 3",
			"Quản trị kinh doanh 4" };

	// Chuyển đổi mảng thành List
	List<String> groupList = Arrays.asList(groupArray);
	private final RoleService roleService;
	private final PostGroupService postGroupService;
	private final UserService userService;
	private final AccountService accountService;
	private final ProfileService profileService;
	private final PasswordEncoder passwordEncoder;

	public ApplicationStartup(RoleService roleService, PostGroupService postGroupService, UserService userService,
			AccountService accountService, ProfileService profileService, PasswordEncoder passwordEncoder) {
		this.roleService = roleService;
		this.postGroupService = postGroupService;
		this.userService = userService;
		this.accountService = accountService;
		this.profileService = profileService;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {
		createRole();
		createGroup();
		createAdmin();
	}

	private void createAdmin() {
		Optional<Account> acOptional = accountService.findByEmail("admin@gmail.com");
		if (acOptional.isEmpty()) {
			User user = new User();
			user.setActive(true);
			user.setUserName("Admin Cute");
			user.setAddress("Gia Lai");
			user.setVerified(true);
			Optional<Role> role = roleService.findByRoleName(RoleName.Admin);
			user.setRole(role.get());

			Account account = new Account();
			account.setEmail("admin@gmail.com");
			account.setActive(true);
			account.setCreatedAt(new Date());
			account.setPassword(passwordEncoder.encode("Admin@111"));
			account.setVerified(true);
			account.setUser(user);

			Profile profile = new Profile();
			profile.setUser(user);

			userService.save(user);
			accountService.save(account);
			profileService.save(profile);

		}

	}

	private void createGroup() {
		for (String groupName : groupList) {
			createGroupIfNotExist(groupName);
		}
	}

	private void createGroupIfNotExist(String groupName) {
		if (postGroupService.findByPostGroupName(groupName).isEmpty()) {
			PostGroup postGroup = new PostGroup();
			postGroup.setPostGroupName(groupName);
			postGroup.setIsPublic(true);
			postGroup.setIsApprovalRequired(true);
			postGroup.setCreateDate(new Date());
			postGroup.setUpdateDate(new Date());
			postGroupService.save(postGroup);
		}
	}

	private void createRole() {
		createRoleIfNotExist(RoleName.SinhVien);
		createRoleIfNotExist(RoleName.GiangVien);
		createRoleIfNotExist(RoleName.Admin);
		createRoleIfNotExist(RoleName.NhanVien);
		createRoleIfNotExist(RoleName.PhuHuynh);
	}

	private void createRoleIfNotExist(RoleName roleName) {
		if (roleService.findByRoleName(roleName).isEmpty()) {
			Role role = new Role();
			role.setRoleName(roleName);
			roleService.save(role);
		}
	}
}
