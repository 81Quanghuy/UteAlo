package vn.iostar.dto;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class AccountManager {

	@Nationalized
	private MultipartFile file;
}
