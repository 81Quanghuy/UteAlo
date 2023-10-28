package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.PostGroupMemberService;
import vn.iostar.service.PostGroupService;

@RestController
@RequestMapping("/api/v1/groupPost/member")
public class MemberPostGroupController {

	@Autowired
	PostGroupService groupService;

	@Autowired
	PostGroupMemberService groupMemberService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@GetMapping("/list")
	public ResponseEntity<GenericResponse> getMemberByPostId(
			@RequestHeader("Authorization") String authorizationHeader) {
		return groupService.getPostGroupJoinByUserId(authorizationHeader);
	}

}
