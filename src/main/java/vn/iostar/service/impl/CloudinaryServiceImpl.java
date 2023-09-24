package vn.iostar.service.impl;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import vn.iostar.service.CloudinaryService;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
	@Autowired
	Cloudinary cloudinary;

	@Override
	public String uploadImage(MultipartFile imageFile) throws IOException {
		
		if (imageFile == null) {
			throw new IllegalArgumentException("File is null. Please upload a valid file.");
		}
		if (!imageFile.getContentType().startsWith("image/")) {
			throw new IllegalArgumentException("Only image files are allowed.");
		}
		
		Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "image");
		Map uploadResult = cloudinary.uploader().upload(imageFile.getBytes(), params);
		return (String) uploadResult.get("secure_url");
	}

	@Override
	public void deleteImage(String imageUrl) throws IOException {
		Map<String, String> params = ObjectUtils.asMap("folder", "Social Media/User", "resource_type", "image");
		Map result = cloudinary.uploader().destroy(getPublicIdImage(imageUrl), params);
		System.out.println(result.get("result").toString());
	}
	
	public String getPublicIdImage(String imageUrl)  {
        String imageName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.lastIndexOf("."));
        String publicId = "Social Media/User/" + imageName;
        return publicId;
	}
	
	
}
