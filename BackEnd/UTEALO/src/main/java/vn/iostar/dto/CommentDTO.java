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
public class CommentDTO {
	private int commentId;
    private PostDTO post;
    private String content;
    private Date createTime;
    private String photos;
    private UserDTO user;
}
