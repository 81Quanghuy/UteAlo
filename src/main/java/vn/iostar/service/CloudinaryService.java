package vn.iostar.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {
	
	String uploadImage(MultipartFile imageFile) throws IOException;
	
	void deleteImage(String imageUrl) throws IOException;
}
