package vn.iostar.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.service.ReportService;

@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
	
	@Autowired
	ReportService reportService;
	
	@PostMapping("/create")
	public ResponseEntity<Object> createUserReport(@ModelAttribute CreatePostRequestDTO requestDTO,
			@RequestHeader("Authorization") String token) {
		return reportService.createUserReport(token, requestDTO);
	}
}
