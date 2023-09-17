package vn.iostar.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vn.iostar.contants.RoleName;
import vn.iostar.contants.RoleUserGroup;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.RegisterRequest;
import vn.iostar.entity.Account;
import vn.iostar.entity.ChatGroup;
import vn.iostar.entity.ChatGroupMember;
import vn.iostar.entity.PostGroup;
import vn.iostar.entity.PostGroupMember;
import vn.iostar.entity.Role;
import vn.iostar.entity.User;
import vn.iostar.entity.VerificationToken;
import vn.iostar.repository.AccountRepository;
import vn.iostar.repository.ChatGroupMemberRepository;
import vn.iostar.repository.ChatGroupRepository;
import vn.iostar.repository.PostGroupMemberRepository;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.repository.RoleRepository;
import vn.iostar.repository.UserRepository;
import vn.iostar.repository.VerificationTokenRepository;
import vn.iostar.service.AccountService;
import vn.iostar.service.EmailVerificationService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	ChatGroupRepository chatGroupRepository;

	@Autowired
	ChatGroupMemberRepository chatGroupMemberRepository;

	@Autowired
	PostGroupRepository postGroupRepository;

	@Autowired
	PostGroupMemberRepository postGroupMemberRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	EmailVerificationService emailVerificationService;

	@Autowired
	VerificationTokenRepository tokenRepository;

	private User userRegister;

	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public ResponseEntity<GenericResponse> userRegister(RegisterRequest registerRequest) {
		if (registerRequest.getPassword().length() < 8 || registerRequest.getPassword().length() > 32)
			throw new RuntimeException("Password must be between 8 and 32 characters long");

		Optional<Account> userOptional = findByPhone(registerRequest.getPhone());
		if (userOptional.isPresent())
			return ResponseEntity.status(409)
					.body(GenericResponse.builder().success(false).message("Phone number already in use").result(null)
							.statusCode(HttpStatus.CONFLICT.value()).build());

		userOptional = findByEmail(registerRequest.getEmail());
		if (userOptional.isPresent())
			return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
					.message("Email already in use").result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
			return ResponseEntity.status(409)
					.body(GenericResponse.builder().success(false).message("Password and confirm password do not match")
							.result(null).statusCode(HttpStatus.CONFLICT.value()).build());

		Optional<Role> role = roleRepository.findByRoleName(RoleName.valueOf(registerRequest.getRoleName()));
		if (!role.isPresent()) {
			return ResponseEntity.status(404).body(GenericResponse.builder().success(false)
					.message("Role name not found").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
		}

		Optional<PostGroup> poOptional = postGroupRepository.findByPostGroupName(registerRequest.getGroupName());
		Optional<ChatGroup> chatOptional = chatGroupRepository.findByGroupName(registerRequest.getGroupName());
		if (poOptional.isPresent() || chatOptional.isPresent()) {
			return ResponseEntity.status(409).body(GenericResponse.builder().success(false)
					.message("Group name already in use").result(null).statusCode(HttpStatus.CONFLICT.value()).build());
		}

		saveUserAndAccount(registerRequest, role.get());
		if (registerRequest.getRoleName().equals(RoleName.SinhVien.name())) {
			saveGroupandRole(registerRequest);
		}
		emailVerificationService.sendOtp(registerRequest.getEmail());

		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Sign Up Success").result(null)
				.statusCode(200).build());
	}

	private void saveGroupandRole(RegisterRequest registerRequest) {
		Date createDate = new Date();

		ChatGroup chatGroup = new ChatGroup();
		chatGroup.setGroupName(registerRequest.getGroupName());
		chatGroup.setCreateDate(createDate);

		ChatGroupMember chatGroupMember = new ChatGroupMember();
		chatGroupMember.setRoleUserGroup(RoleUserGroup.valueOf(registerRequest.getRoleUserGroup()));
		chatGroupMember.setUser(userRegister);
		chatGroupMember.getChatGroup().add(chatGroup);
		chatGroup.getChatGroupMembers().add(chatGroupMember);

		PostGroup postGroup = new PostGroup();
		postGroup.setCreateDate(createDate);
		postGroup.setPostGroupName(registerRequest.getGroupName());

		PostGroupMember postGroupMember = new PostGroupMember();
		postGroupMember.setRoleUserGroup(RoleUserGroup.valueOf(registerRequest.getRoleUserGroup()));
		postGroupMember.setUser(userRegister);
		postGroupMember.getPostGroup().add(postGroup);
		postGroup.getPostGroupMembers().add(postGroupMember);

		chatGroupRepository.save(chatGroup);
		chatGroupMemberRepository.save(chatGroupMember);
		postGroupRepository.save(postGroup);
		postGroupMemberRepository.save(postGroupMember);
	}

	public <S extends Account> S save(S entity) {
		return accountRepository.save(entity);
	}

	public Optional<Account> findByEmail(String email) {
		return accountRepository.findByEmail(email);
	}

	public Optional<Account> findByPhone(String phone) {
		return accountRepository.findByPhone(phone);
	}

	@Override
	public String validateVerificationAccount(String token) {
		VerificationToken verificationToken = tokenRepository.findByToken(token);
		if (verificationToken == null) {
			return "Invalid token, please check the token again!";
		}
		User user = verificationToken.getUser();
		user.setVerified(true);
		userRepository.save(user);
		return "Account verification successful, please login!";
	}

	public void saveUserAndAccount(RegisterRequest registerRequest, Role role) {

		User user = new User();
		user.setPhone(registerRequest.getPhone());
		user.setUserName(registerRequest.getFullName());
		user.setRole(role);
		userRegister = user;
		userRepository.save(user);

		Account account = new Account();
		account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		account.setPhone(registerRequest.getPhone());
		account.setEmail(registerRequest.getEmail());

		Date createDate = new Date();
		account.setCreatedAt(createDate);
		account.setUpdatedAt(createDate);

		account.setUser(user);
		accountRepository.save(account);

	}

}
