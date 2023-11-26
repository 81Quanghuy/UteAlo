package Friends;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.iostar.UtealoApplication;
import vn.iostar.controller.user.FriendController;
import vn.iostar.dto.FriendResponse;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Friend;
import vn.iostar.entity.User;
import vn.iostar.service.FriendRequestService;
import vn.iostar.service.FriendService;
import vn.iostar.service.impl.FriendRequestServiceImpl;
import vn.iostar.service.impl.FriendServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UtealoApplication.class)
class GetListTest {

    @Mock
    private FriendService friendService = new FriendServiceImpl();

    @Mock
    private Friend friend;

    @Mock
    private User user;

    @Mock
    private FriendRequestService friendRequestService = new FriendRequestServiceImpl();

    @InjectMocks
    private FriendController friendController;

    // Test case 1: Kiểm tra xem có lấy được danh sách bạn bè không
    @Test
    public void testGetListFriend() {

        // Arrange
        List<FriendResponse>  friendResponses= new ArrayList<>();
        FriendResponse friendResponse = FriendResponse.builder()
                        .userId("5c53074e-3488-4ef8-99a3-fd3941330489")
                        .background("https://iostar.com.vn/images/2021/05/12/anh-bia-1.jpg")
                        .avatar("https://iostar.com.vn/images/2021/05/12/anh-dai-dien-1.jpg")
                        .username("QuangHuy")
                        .build();
        friendResponses.add(friendResponse);
        FriendResponse friendResponse1 = FriendResponse.builder()
                .userId("5c53074e-3488-4ef8-99a3-fd3941330489")
                .background("https://iostar.com.vn/images/2021/05/12/anh-bia-1.jpg")
                .avatar("https://iostar.com.vn/images/2021/05/12/anh-dai-dien-1.jpg")
                .username("HongKhang")
                .build();
        friendResponses.add(friendResponse1);
        String userId = "5c53074e-3488-4ef8-99a3-fd3941330489";

        // Stub the service method to return an empty list
        when(friendService.findFriendUserIdsByUserId(userId))
                .thenReturn(friendResponses);

        // Act
        ResponseEntity<GenericResponse> response = friendController.getListFriendByUserId(userId);

        // Assert
        assertNotNull(response.getBody().getResult());

        // Check the type of the result
        assertTrue(response.getBody().getResult() instanceof List);

        // Verify that the service method was called with the correct arguments
        verify(friendService, times(1)).findFriendUserIdsByUserId(userId);
    }

    // Test case 2: Kiểm tra chức năng gửi lời mời kết bạn
    @Test
    public void testSendFriendRequest() {
        // Arrange
        String userId = "5c53074e-3488-4ef8-99a3-fd3941330489";
        String friendId = "5c53074e-3488-4ef8-99a3-fd3941330489";

        // Stub the service method to return an empty list
        when(friendRequestService.sendFriendRequest(userId, friendId))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .message("Create Successful!")
                        .result(null)
                        .statusCode(200)
                        .build()));

        // Act
        ResponseEntity<GenericResponse> response = friendRequestService.sendFriendRequest(userId, friendId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getResult());
        assertEquals("Create Successful!", response.getBody().getMessage());

        // Verify that the service method was called with the correct arguments
        verify(friendRequestService, times(1)).sendFriendRequest(userId, friendId);
    }
    // Test case 3: Kiểm tra chức năng xóa bạn bè
    @Test
    public void testDeleteFriend() throws Exception {
        // Arrange
        String userId = "5c53074e-3488-4ef8-99a3-fd3941330489";
        String friendId = "5c53074e-3488-4ef8-99a3-fd3941330489";

        // Stub the service method to return an empty list
        when(friendService.deleteFriend(userId, friendId))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .message("Delete Successful!")
                        .result(null)
                        .statusCode(200)
                        .build()));

        // Act
        ResponseEntity<GenericResponse> response = friendService.deleteFriend(userId, friendId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getResult());
        assertEquals("Delete Successful!", response.getBody().getMessage());

        // Verify that the service method was called with the correct arguments
        verify(friendService, times(1)).deleteFriend(userId, friendId);
    }
    // Test case 4: Kiểm tra chức năng chấp nhận lời mời kết bạn MockMVC và MockResult
    @Test
    public void testAcceptFriendRequest() {
        // Arrange
        String userId = "5c53074e-3488-4ef8-99a3-fd3941330489";
        String friendId = "5c53074e-3488-4ef8-99a3-fd3941330489";

        // Stub the service method to return an empty list
        when(friendRequestService.acceptRequest(userId, friendId))
                .thenReturn(ResponseEntity.ok(GenericResponse.builder()
                        .success(true)
                        .message("Accept Successful!")
                        .result(null)
                        .statusCode(200)
                        .build()));

        // Act
        ResponseEntity<GenericResponse> response = friendRequestService.acceptRequest(userId, friendId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody().getResult());
        assertEquals("Accept Successful!", response.getBody().getMessage());

        // Verify that the service method was called with the correct arguments
        verify(friendRequestService, times(1)).acceptRequest(userId, friendId);
    }
}
