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
public class PostDTO {
	private int postId;
	private Date postTime;
	private String content;
	private String photos;
	private UserDTO user;
	private PostGroupDTO postGroup;
}
