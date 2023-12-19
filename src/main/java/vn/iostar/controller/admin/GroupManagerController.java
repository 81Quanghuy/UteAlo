package vn.iostar.controller.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CombinedGroupResponse;
import vn.iostar.dto.CountDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.GroupPostResponse;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.dto.SearchPostGroup;
import vn.iostar.repository.PostGroupRepository;
import vn.iostar.service.PostGroupService;

@RestController
@RequestMapping("/api/v1/admin/groupManager")
public class GroupManagerController {

	@Autowired
	PostGroupService postGroupService;

	@Autowired
	PostGroupRepository postGroupRepository;

	// Lấy danh sách tất cả các nhóm trong hệ thống
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllGroups(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
		return postGroupService.getAllGroups(authorizationHeader, page, items);
	}

	// Admin xóa nhóm trong hệ thống
	@PutMapping("/delete/{postGroupId}")
	public ResponseEntity<GenericResponse> deleteCommentOfPost(
			@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("postGroupId") Integer postGroupId) {
		return postGroupService.deletePostGroupByAdmin(postGroupId, authorizationHeader);
	}

	// Admin tạo nhóm
	@PostMapping("/create")
	public ResponseEntity<GenericResponse> createGroupBygroup(@RequestBody PostGroupDTO postGroup,
			@RequestHeader("Authorization") String authorizationHeader) {
		return postGroupService.createPostGroupByAdmin(postGroup, authorizationHeader);
	}

	// Đếm số lượng nhóm từng tháng trong năm
	@GetMapping("/countGroupsByMonthInYear")
	public ResponseEntity<Map<String, Long>> countGroupsByMonthInYear() {
		try {
			Map<String, Long> groupCountsByMonth = postGroupService.countGroupsByMonthInYear();
			return ResponseEntity.ok(groupCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng group
	@GetMapping("/countGroup")
	public ResponseEntity<CountDTO> countGroupsToday() {
		try {
			long groupCountToDay = postGroupService.countGroupsToday();
			long groupCountInWeek = postGroupService.countGroupsInWeek();
			long groupCountIn1Month = postGroupService.countGroupsInMonthFromNow();
			long groupCountIn3Month = postGroupService.countGroupsInThreeMonthsFromNow();
			long groupCountIn6Month = postGroupService.countGroupsInSixMonthsFromNow();
			long groupCountIn9Month = postGroupService.countGroupsInNineMonthsFromNow();
			long groupCountIn1Year = postGroupService.countGroupsInOneYearFromNow();

			CountDTO groupCountDTO = new CountDTO(groupCountToDay, groupCountInWeek, groupCountIn1Month,
					groupCountIn3Month, groupCountIn6Month, groupCountIn9Month, groupCountIn1Year);
			return ResponseEntity.ok(groupCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Lấy danh sách nhóm mà 1 user tham gia có phân trang
	@GetMapping("/listGroup/{userId}")
	public ResponseEntity<GenericResponseAdmin> getPostGroupJoinByUserId(@PathVariable("userId") String userId,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int itemsPerPage) {

		return postGroupService.getPostGroupJoinByUserId(userId, page, itemsPerPage);
	}

	// Thống kê bài post trong ngày hôm nay
	// Thống kê bài post trong 1 ngày
	// Thống kê bài post trong 7 ngày
	// Thống kê bài post trong 1 tháng
	@GetMapping("/filterByDate")
	public List<SearchPostGroup> getGroups(@RequestParam(required = false) String action,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		switch (action != null ? action.toLowerCase() : "") {
		case "today":
			return postGroupService.getGroupsToday();
		case "7days":
			return postGroupService.getGroupsIn7Days();
		case "month":
			return postGroupService.getGroupsInMonth();
		default:
			// Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
			// hoặc một giá trị mặc định
			break;
		}
		// Trả về null hoặc danh sách rỗng tùy theo logic của bạn
		return null;
	}

	@GetMapping("/userJoin/{userId}")
	public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(@PathVariable("userId") String userId) {
		List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserId(userId);
		return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get list group join successfully!")
				.result(list).statusCode(HttpStatus.OK.value()).build());
	}

	@GetMapping("/userOwner/{userId}")
	public ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(@PathVariable("userId") String userId) {
		List<GroupPostResponse> list = postGroupRepository.findPostGroupInfoByUserIdOfUser(userId);
		return ResponseEntity
				.ok(GenericResponse.builder().success(true).message("Get list group post owner successfully!")
						.result(list).statusCode(HttpStatus.OK.value()).build());
	}
	
//	@GetMapping("/combinedGroups/{userId}")
//	public ResponseEntity<GenericResponse> getCombinedGroupsByUserId(@PathVariable("userId") String userId) {
//	    List<GroupPostResponse> joinGroups = postGroupRepository.findPostGroupInfoByUserId(userId);
//	    List<GroupPostResponse> ownerGroups = postGroupRepository.findPostGroupInfoByUserIdOfUser(userId);
//
//	    CombinedGroupResponse combinedResponse = new CombinedGroupResponse();
//	    combinedResponse.setJoinGroups(joinGroups);
//	    combinedResponse.setOwnerGroups(ownerGroups);
//
//	    return ResponseEntity.ok(GenericResponse.builder()
//	            .success(true)
//	            .message("Get combined list of groups successfully!")
//	            .result(combinedResponse)
//	            .statusCode(HttpStatus.OK.value())
//	            .build());
//	}
	@GetMapping("/combinedGroups/{userId}")
	public ResponseEntity<GenericResponse> getCombinedGroupsByUserId(@PathVariable("userId") String userId) {
	    List<GroupPostResponse> joinGroups = postGroupRepository.findPostGroupInfoByUserId(userId);
	    List<GroupPostResponse> ownerGroups = postGroupRepository.findPostGroupInfoByUserIdOfUser(userId);

	    List<GroupPostResponse> combinedGroups = new ArrayList<>();
	    combinedGroups.addAll(joinGroups);
	    combinedGroups.addAll(ownerGroups);

	    return ResponseEntity.ok(GenericResponse.builder()
	            .success(true)
	            .message("Get combined list of groups successfully!")
	            .result(combinedGroups)
	            .statusCode(HttpStatus.OK.value())
	            .build());
	}

}
