package vn.iostar.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import vn.iostar.dto.CreatePostRequestDTO;
import vn.iostar.entity.Report;

public interface ReportService {

	void deleteAll();

	void deleteById(Integer id);

	long count();

	List<Report> findAll();

	<S extends Report> S save(S entity);

	ResponseEntity<Object> createUserReport(String token, CreatePostRequestDTO requestDTO);

	ResponseEntity<Object> getReportById(String currentUserId, Integer reportId);
}
