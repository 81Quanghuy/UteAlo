package vn.iostar.dto;



import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;



@Data
public class PostUpdateRequest {
	@Nationalized
    @NotBlank(message = "Content is required")
    private String content;

    @Nationalized
    @NotBlank(message = "Location is required")
    private String location;

    
    @Nationalized
    private MultipartFile photos;
    
    @Nationalized
    private MultipartFile files;
    
    @Nationalized
    private Integer postGroupId;
    

}
