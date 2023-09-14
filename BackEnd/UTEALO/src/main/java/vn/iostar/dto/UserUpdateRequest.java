package vn.iostar.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.util.Date;

@Data
public class UserUpdateRequest {
    @Nationalized
    @NotBlank(message = "Full name is required")
    private String fullName;

    @Nationalized
    @NotBlank(message = "Address is required")
    private String address;

    private Date dateOfBirth;

    @Nationalized
    private String about;

}
