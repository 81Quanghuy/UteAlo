package Friends;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import vn.iostar.UtealoApplication;
import vn.iostar.dto.FriendRequestResponse;
import vn.iostar.entity.Friend;
import vn.iostar.repository.FriendRepository;
import vn.iostar.service.FriendService;
import vn.iostar.service.impl.FriendServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UtealoApplication.class)
class GetListTest {

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private FriendService friendService = new FriendServiceImpl();

    @Test
    void getListTest() {
        // Thiết lập hành vi giả định (stub) cho friendRepository
        when(friendRepository.findFriendUserIdsByUserId("1")).thenReturn(new ArrayList<FriendRequestResponse>());

        // Gọi phương thức của myService, sử dụng dữ liệu giả định từ myRepository
        List<FriendRequestResponse> result = friendService.findFriendUserIdsByUserId("1");

        // Kiểm tra kết quả
        assert (result.isEmpty());

        // So sánh kết quả trả về từ service với kết quả mong đợi
        verify(friendRepository, times(1)).findFriendUserIdsByUserId("1");

    }
}
