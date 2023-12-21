package vn.iostar.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.ReportsResponse;
import vn.iostar.entity.Report;
import vn.iostar.entity.User;
import vn.iostar.exception.wrapper.NotFoundException;
import vn.iostar.repository.ReportRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.ReportService;
import vn.iostar.service.UserService;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	ReportRepository reportRepository;

	@Autowired
	JwtTokenProvider jwtTokenProvider;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	UserService userService;

	@Override
	public <S extends Report> S save(S entity) {
		return reportRepository.save(entity);
	}

	@Override
	public List<Report> findAll() {
		return reportRepository.findAll();
	}

	@Override
	public long count() {
		return reportRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		reportRepository.deleteById(id);
	}

	@Override
	public void deleteAll() {
		reportRepository.deleteAll();
	}

	@Override
	public ResponseEntity<Object> createUserReport(String token, CreatePostRequestDTO requestDTO) {
		List<String> allowedFileExtensions = Arrays.asList("docx", "txt", "pdf");

		if (requestDTO.getLocation() == null && requestDTO.getContent() == null) {
			return ResponseEntity.badRequest().body("Please provide all required fields.");
		}

		String jwt = token.substring(7);
		String userId = jwtTokenProvider.getUserIdFromJwt(jwt);
		Optional<User> user = userService.findById(userId);

		Report post = new Report();
		post.setContent(requestDTO.getContent());
		post.setPrivacyLevel(requestDTO.getPrivacyLevel());

		try {
			if (requestDTO.getPhotos() == null || requestDTO.getPhotos().getContentType() == null) {
				post.setPhotos("");
			} else {
				post.setPhotos(cloudinaryService.uploadImage(requestDTO.getPhotos()));
			}
			if (requestDTO.getFiles() == null || requestDTO.getFiles().getContentType() == null) {
				post.setFiles("");
			} else {
				String fileExtension = StringUtils.getFilenameExtension(requestDTO.getFiles().getOriginalFilename());
				if (fileExtension != null && allowedFileExtensions.contains(fileExtension.toLowerCase())) {
					post.setFiles(cloudinaryService.uploadFile(requestDTO.getFiles()));
				} else {
					throw new IllegalArgumentException("Not support for this file.");
				}
			}
		} catch (IOException e) {
			// Xử lý ngoại lệ nếu có
			e.printStackTrace();
		}

		if (user.isEmpty()) {
			return ResponseEntity.badRequest().body("User not found");
		} else {
			post.setUser(user.get());
		}

		// Thiết lập các giá trị cố định
		post.setPostTime(new Date());

		// Tiếp tục xử lý tạo bài đăng
		save(post);
		ReportsResponse postsResponse = new ReportsResponse(post);

		GenericResponse response = GenericResponse.builder().success(true).message("Report Created Successfully")
				.result(postsResponse).statusCode(200).build();

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<Object> getReportById(String currentUserId, Integer reportId) {
		Optional<User> user = userService.findById(currentUserId);
		if (user.isEmpty()) {
			throw new NotFoundException("User not found");
		}
		Optional<Report> repoOptional = reportRepository.findById(reportId);
		if (repoOptional.isPresent()) {
			ReportsResponse reportsResponse = new ReportsResponse(repoOptional.get());

			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
							.result(reportsResponse).statusCode(HttpStatus.OK.value()).build());
		}
		throw new NotFoundException("Report not found");

	}

}
