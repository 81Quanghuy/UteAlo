package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String userId;
    private String userName;
    private String address;
    private String phone;
    private String gender;
    private Date dayOfBirth;
    private boolean status;
    private RoleDTO role;
    private Boolean isOnline;
}
