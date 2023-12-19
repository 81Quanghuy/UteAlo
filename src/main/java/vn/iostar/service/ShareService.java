package vn.iostar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.GenericResponseAdmin;
import vn.iostar.dto.SharePostRequestDTO;
import vn.iostar.dto.SharesResponse;
import vn.iostar.entity.Share;

public interface ShareService {

	void deleteAll();

	void delete(Share entity);

	long count();

	Optional<Share> findById(Integer id);

	List<Share> findAll();

	<S extends Share> S save(S entity);

	ResponseEntity<GenericResponse> getShare(Integer shareId);

	ResponseEntity<Object> sharePost(String token, SharePostRequestDTO requestDTO);

	ResponseEntity<Object> updateSharePost(SharePostRequestDTO requestDTO, String currentUserId);

	ResponseEntity<GenericResponse> deleteSharePost(Integer shareId, String token, String userId);

	ResponseEntity<GenericResponse> getShareOfPostGroup(String currentUserId, Pageable pageable);

	SharesResponse getSharePost(Share share, String currentUserId);

	List<SharesResponse> findUserSharePosts(String currentUserId, String userId, Pageable pageable);

	List<SharesResponse> findMySharePosts(String currentUserId, Pageable pageable);

	List<SharesResponse> findSharesByUserAndFriendsAndGroupsOrderByPostTimeDesc(String userId, Pageable pageable);

	List<SharesResponse> findPostGroupShares(String currentUserId, Integer postGroupId, Pageable pageable);

	ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, Integer postGroupId, Integer page,
			Integer size);

	// Tìm tất cả bài Shares trong hệ thống
	Page<SharesResponse> findAllShares(int page, int itemsPerPage);

	// Lấy tất cả bài Shares trong hệ thống
	ResponseEntity<GenericResponseAdmin> getAllShares(String authorizationHeader, int page, int itemsPerPage);

	// Admin xóa bài Shares trong hệ thống
	ResponseEntity<GenericResponse> deleteShareByAdmin(Integer shareId, String authorizationHeader);

	// Thống kê bài Shares trong ngày hôm nay
	List<SharesResponse> getSharesToday();

	// Thống kê bài Shares trong 1 ngày
	List<SharesResponse> getSharesInDay(Date day);

	// Thống kê bài Shares trong 7 ngày
	List<SharesResponse> getSharesIn7Days();

	// Thống kê bài Shares trong 1 tháng
	List<SharesResponse> getSharesInMonth(Date month);

	// Đếm số lượng bài Shares trong ngày hôm nay
	long countSharesToday();

	// Đếm số lượng bài Shares trong 7 ngày
	public long countSharesInWeek();

	// Đếm số lượng bài Shares trong 1 tháng
	long countSharesInMonthFromNow();

	// Đếm số lượng bài Shares trong 1 năm
	long countSharesInOneYearFromNow();

	// Đếm số lượng bài Shares trong 9 tháng
	long countSharesInNineMonthsFromNow();

	// Đếm số lượng bài Shares trong 6 tháng
	long countSharesInSixMonthsFromNow();

	// Đếm số lượng bài Shares trong 3 tháng
	long countSharesInThreeMonthsFromNow();

	// Đếm số lượng bài Shares từng tháng trong năm
	Map<String, Long> countSharesByMonthInYear();

	// Đếm số lượng bài Shares của user từng tháng trong năm
	Map<String, Long> countSharesByUserMonthInYear(String userId);

	// Thống kê bài Shares trong 1 tháng
	List<SharesResponse> getSharesIn1Month();

	ResponseEntity<GenericResponse> getTimeLineSharePosts(String currentUserId, Integer page, Integer size);

	ResponseEntity<GenericResponse> getShare(String currentUserId, Integer shareId);

	// Lấy tất cả bài share của 1 user có phân trang
	Page<SharesResponse> findAllSharesByUserId(int page, int itemsPerPage, String userId);

	// Lấy tất cả bài share của 1 user trong 1 tháng có phân trang
	Page<SharesResponse> findAllSharesInMonthByUserId(int page, int itemsPerPage, String userId);
}
