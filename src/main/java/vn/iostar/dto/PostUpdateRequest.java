package vn.iostar.dto;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import vn.iostar.contants.PrivacyLevel;

@Data
public class PostUpdateRequest {
	
	@Nationalized
    @NotBlank(message = "Content is required")
    private String content;

    @Nationalized
    @NotBlank(message = "Location is required")
    private String location;

    private String photoUrl;
    private String fileUrl;
    
    @Nationalized
    private MultipartFile photos;
    
    @Nationalized
    private MultipartFile files;
    
    @Nationalized
    private PrivacyLevel privacyLevel;
    
}
