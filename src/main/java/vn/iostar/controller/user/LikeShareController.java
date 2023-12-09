package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.repository.LikeRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.LikeService;

@RestController
@RequestMapping("/api/v1/share/like")
public class LikeShareController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	LikeService likeService;

	@Autowired
	LikeRepository likeRepository;

	@GetMapping("/{shareId}")
	public ResponseEntity<GenericResponse> getLikeOfShare(@PathVariable("shareId") int shareId) {
		return likeService.getLikeOfShare(shareId);
	}

	@PostMapping("/toggleLike/{shareId}")
	public ResponseEntity<Object> toggleLikeShare(@PathVariable("shareId") int shareId,
			@RequestHeader("Authorization") String token) {
		return likeService.toggleLikeShare(token, shareId);
	}

	@GetMapping("/checkUser/{shareId}")
	public ResponseEntity<Object> checkUserLikePost(@PathVariable("shareId") int shareId,
			@RequestHeader("Authorization") String token) {
		return likeService.checkUserLikeShare(token, shareId);
	}

	// Lấy danh sách những người đã like comment
	@GetMapping("/listUser/{shareId}")
	public ResponseEntity<Object> listUserLikeShare(@PathVariable("shareId") int shareId) {
		return likeService.listUserLikeShare(shareId);
	}

}
