package vn.iostar.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.iostar.entity.DateEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesOfGroupDTO extends DateEntity {
    private String userId;
    private String userName;
    private String files;
    private int postId;
    private String type;

    public FilesOfGroupDTO(String userId, String userName, String files, Integer postId, Date createAt, Date updateAt) {
        this.userId = userId;
        this.userName = userName;
        this.files = files;
        this.postId = postId;
        this.setCreateAt(createAt);
        this.setUpdateAt(updateAt);
    }
}
