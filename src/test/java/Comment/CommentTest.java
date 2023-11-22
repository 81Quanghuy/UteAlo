package Comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vn.iostar.UtealoApplication;
import vn.iostar.controller.user.CommentPostController;
import vn.iostar.dto.CommentPostResponse;
import vn.iostar.dto.CreateCommentPostRequestDTO;
import vn.iostar.dto.GenericResponse;
import vn.iostar.entity.Comment;
import vn.iostar.repository.CommentRepository;
import vn.iostar.security.JwtTokenProvider;
import vn.iostar.service.CommentService;
import vn.iostar.service.impl.CommentServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UtealoApplication.class)
public class CommentTest {
    @Mock
    Comment comment;

    @Mock
    CommentRepository commentRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    CommentService commentService = new CommentServiceImpl();

    @InjectMocks
    CommentPostController commentPostController;

    @Test
    //Đăng bình luận thành công
    public void testCommentPostSuccess() {
        CreateCommentPostRequestDTO requestDTO = new CreateCommentPostRequestDTO();
        String token= "";
        // CommentPostResponse commentPostResponse = new CommentPostResponse(comment);
        GenericResponse response = GenericResponse.builder().success(true).message("Comment Post Successfully")
                .result(null)
                .statusCode(200).build();

        when(commentService.createCommentPost(token,requestDTO)).thenReturn(ResponseEntity.ok(response));

        //Act
        ResponseEntity<Object> responseEntity = commentPostController.createCommentPost(requestDTO, token);

        //Assert
        assertEquals(OK, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), response);

        verify(commentService).createCommentPost(token,requestDTO);
    }
}
