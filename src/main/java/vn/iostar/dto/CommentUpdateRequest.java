package vn.iostar.dto;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentUpdateRequest {
	
	@Nationalized
    @NotBlank(message = "Content is required")
    private String content;
	
	@Nationalized
    private MultipartFile photos;
}
