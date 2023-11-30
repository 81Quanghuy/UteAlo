package vn.iostar.controller.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.SharePostRequestDTO;
import vn.iostar.dto.SharesResponse;
import vn.iostar.entity.Share;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.ShareService;
import vn.iostar.service.UserService;

@RestController
@RequestMapping("/api/v1/share")
public class ShareController {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    ShareService shareService;

    @Autowired
    UserService userService;

    @GetMapping("/{shareId}")
    public ResponseEntity<GenericResponse> getShareByShareId(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable("shareId") Integer shareId) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        Optional<Share> share = shareService.findById(shareId);

        if (share.isEmpty()) {
            throw new RuntimeException("Post not found.");
        } else if (currentUserId.equals(share.get().getUser().getUserId())) {
            SharesResponse sharePosts = shareService.getSharePost(share.get(), currentUserId);
            return ResponseEntity.ok(
                    GenericResponse.builder().success(true).message("Retrieving share post successfully and access update")
                            .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
        } else {
            return ResponseEntity.ok(GenericResponse.builder().success(true)
                    .message("Retrieving share post successfully and access update denied")
                    .statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy những bài share của user theo UserId
    @GetMapping("/{userId}/post")
    public ResponseEntity<GenericResponse> getShareByUserId(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("userId") String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        Pageable pageable = PageRequest.of(page, size);
        List<SharesResponse> sharePosts = shareService.findUserSharePosts(currentUserId, userId, pageable);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving share post successfully")
                .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
    }

    // Lấy những bài share của minh
    @GetMapping("/post")
    public ResponseEntity<GenericResponse> getMyShare(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        Pageable pageable = PageRequest.of(page, size);
        List<SharesResponse> sharePosts = shareService.findMySharePosts(currentUserId, pageable);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieving share post successfully")
                .result(sharePosts).statusCode(HttpStatus.OK.value()).build());
    }

    // Lấy những bài share liên quan đến mình như: nhóm, bạn bè, cá nhân
    @GetMapping("/get/timeLine")
    public ResponseEntity<GenericResponse> getShareTimeLine(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        return shareService.getTimeLineSharePosts(currentUserId, page, size);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createSharePost(@RequestBody SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String token) {
        return shareService.sharePost(token, requestDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateSharePost(@RequestBody SharePostRequestDTO requestDTO,
                                                  @RequestHeader("Authorization") String authorizationHeader,
                                                  BindingResult bindingResult) throws Exception {

        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);

        return shareService.updateSharePost(requestDTO, currentUserId);

    }

    @PutMapping("/delete/{shareId}")
    public ResponseEntity<GenericResponse> deleteSharePost(@RequestHeader("Authorization") String token,
                                                           @PathVariable("shareId") Integer shareId, @RequestBody String userId) {
        return shareService.deleteSharePost(shareId, token, userId);

    }

    // Lấy tất cả các bài share của những nhóm mình tham gia
    @GetMapping("/inGroup")
    public ResponseEntity<GenericResponse> getShareOfUserPostGroup(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
        Pageable pageable = PageRequest.of(page, size);
        return shareService.getShareOfPostGroup(currentUserId, pageable);

    }
}
