package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.ReportService;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {

	@Autowired
	ReportService reportService;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@PostMapping("/create")
	public ResponseEntity<Object> createUserReport(@ModelAttribute CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return reportService.createUserReport(token, requestDTO);
	}

	// Lấy report theo Id
	@GetMapping("/get/{reportId}")
	public ResponseEntity<Object> getReportById(@RequestHeader("Authorization") String authorizationHeader,
			@PathVariable("reportId") Integer reportId) {
		String token = authorizationHeader.substring(7);
		String currentUserId = jwtTokenProvider.getUserIdFromJwt(token);
		return reportService.getReportById(currentUserId, reportId);
	}
}
