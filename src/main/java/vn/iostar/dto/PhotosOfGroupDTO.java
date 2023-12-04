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
public class PhotosOfGroupDTO {
	private String userId;
	private String userName;
	private String photos;
	private int postGroupId;
	private String postGroupName;
	private int postId;
}
