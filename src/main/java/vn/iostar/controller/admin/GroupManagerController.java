package vn.iostar.controller.admin;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import vn.iostar.dto.CountDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.PostGroupDTO;
import vn.iostar.service.PostGroupService;

@RestController
@RequestMapping("/api/v1/admin/groupManager")
public class GroupManagerController {

	@Autowired
	PostGroupService postGroupService;

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

			CountDTO groupCountDTO = new CountDTO(groupCountToDay, groupCountInWeek, groupCountIn1Month, groupCountIn3Month,
					groupCountIn6Month, groupCountIn9Month, groupCountIn1Year);
			return ResponseEntity.ok(groupCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
