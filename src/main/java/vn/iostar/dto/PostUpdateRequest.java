package vn.iostar.dto;



import java.util.Date;

import org.hibernate.annotations.Nationalized;

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

    private Date updateAt;
    
    @Nationalized
    private String photos;
    
    @Nationalized
    private Integer postGroupId;
    

}
