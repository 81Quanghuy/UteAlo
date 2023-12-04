package vn.iostar.dto;

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
public class FilesOfGroupDTO {
	private String userId;
	private String userName;
	private String files;
	private int postGroupId;
	private String postGroupName;
	private int postId;
}
