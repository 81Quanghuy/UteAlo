package vn.iostar.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CountDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.SharesResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.ShareService;

@RestController
@RequestMapping("/api/v1/admin/shareManager")
public class ShareManagerController {

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	ShareService shareService;

	// Lấy tất cả bài share post trong hệ thống
	@GetMapping("/list")
	public ResponseEntity<GenericResponseAdmin> getAllShares(@RequestHeader("Authorization") String authorizationHeader,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int items) {
		return shareService.getAllShares(authorizationHeader, page, items);
	}

	// Xóa bài share post trong hệ thống
	@PutMapping("/delete/{shareId}")
	public ResponseEntity<GenericResponse> deleteUser(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("shareId") Integer shareId) {
		return shareService.deleteShareByAdmin(shareId, authorizationHeader);
	}

	// Thống kê bài share post trong ngày hôm nay
	// Thống kê bài share post trong 1 ngày
	// Thống kê bài share post trong 7 ngày
	// Thống kê bài share post trong 1 tháng
	@GetMapping("/filterByDate")
	public List<SharesResponse> getShares(@RequestParam(required = false) String action,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		switch (action != null ? action.toLowerCase() : "") {
		case "today":
			return shareService.getSharesToday();
		case "day":
			if (date != null) {
				return shareService.getSharesInDay(date);
			}
			break;
		case "7days":
			return shareService.getSharesIn7Days();
		case "month":
			return shareService.getSharesIn1Month();
		default:
			// Nếu không có action hoặc action không hợp lệ, có thể trả về thông báo lỗi
			// hoặc một giá trị mặc định
			break;
		}
		// Trả về null hoặc danh sách rỗng tùy theo logic của bạn
		return null;
	}

	// Đếm số lượng bài share post
	@GetMapping("/countShare")
	public ResponseEntity<CountDTO> countPostsToday() {
		try {
			long shareCountToDay = shareService.countSharesToday();
			long shareCountInWeek = shareService.countSharesInWeek();
			long shareCountIn1Month = shareService.countSharesInMonthFromNow();
			long shareCountIn3Month = shareService.countSharesInThreeMonthsFromNow();
			long shareCountIn6Month = shareService.countSharesInSixMonthsFromNow();
			long shareCountIn9Month = shareService.countSharesInNineMonthsFromNow();
			long shareCountIn1Year = shareService.countSharesInOneYearFromNow();

			CountDTO shareCountDTO = new CountDTO(shareCountToDay, shareCountInWeek, shareCountIn1Month,
					shareCountIn3Month, shareCountIn6Month, shareCountIn9Month, shareCountIn1Year);
			return ResponseEntity.ok(shareCountDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// Đếm số lượng bài share post từng tháng trong năm
	@GetMapping("/countSharesByMonthInYear")
	public ResponseEntity<Map<String, Long>> countSharesByMonthInYear() {
		try {
			Map<String, Long> shareCountsByMonth = shareService.countSharesByMonthInYear();
			return ResponseEntity.ok(shareCountsByMonth);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
