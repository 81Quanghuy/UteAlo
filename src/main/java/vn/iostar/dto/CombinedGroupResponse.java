package vn.iostar.dto;

import java.util.List;

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
public class CombinedGroupResponse {
    private List<GroupPostResponse> joinGroups;
    private List<GroupPostResponse> ownerGroups;
}
