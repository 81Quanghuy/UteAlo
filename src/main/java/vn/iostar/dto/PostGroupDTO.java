package vn.iostar.dto;

import java.util.Set;

import org.hibernate.annotations.Nationalized;
import org.springframework.web.multipart.MultipartFile;

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
public class PostGroupDTO {
	private Integer postGroupId;
	private String postGroupName;
	private String bio;
	private Set<String> userId;
	private boolean isPublic;// true: private, false: public
	private boolean isApprovalRequired;

	@Nationalized
	private MultipartFile avatar;

	@Nationalized
	private MultipartFile background;
}
