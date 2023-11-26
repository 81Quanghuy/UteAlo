package Comment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(MockitoExtension.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UtealoApplication.class)
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
    @Test
    // Đăng bình luận thất bại
    public void testCommentPostFailure() {
        // Arrange
        CreateCommentPostRequestDTO requestDTO = new CreateCommentPostRequestDTO();
        String token = "";

        GenericResponse response = GenericResponse.builder()
                .success(false)
                .message("Comment Post Failed")
                .result(null)
                .statusCode(400)
                .build();

        when(commentService.createCommentPost(token, requestDTO))
                .thenReturn(ResponseEntity.status(BAD_REQUEST).body(response));

        // Act
        ResponseEntity<Object> responseEntity = commentPostController.createCommentPost(requestDTO, token);

        // Assert
        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(responseEntity.getBody(), response);

        verify(commentService).createCommentPost(token, requestDTO);
    }
    @Test
    public void deleteCommentOfPost_Success() {
        // Arrange
        Integer commentId = 1;
        
        Comment comment = new Comment(); // create a Comment object or use a builder if available
        comment.setCommentId(1);
        comment.setContent("");
        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        ResponseEntity<GenericResponse> responseEntity = commentService.deleteCommentOfPost(commentId);
        

        // Assert
        assertNotNull(responseEntity);
       // assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
       // assertTrue(responseEntity.getBody().getSuccess());
      //  assertEquals("Delete Successful!", responseEntity.getBody().getMessage());

        // Verify interaction with the commentRepository
        verify(commentRepository, times(1)).findById(commentId);

    }

    @Test
    public void deleteCommentOfPost_CommentNotFound() {
        // Arrange
        Integer commentId = 1;

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
   //     Mockito.when(commentRepository.delete(comment)).thenReturn(true);

        // Act
        ResponseEntity<GenericResponse> responseEntity = commentService.deleteCommentOfPost(commentId);

        // Assert
      //  assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
      //  assertFalse(responseEntity.getBody().getSuccess());
      //  assertEquals("Cannot found comment!", responseEntity.getBody().getMessage());
        assertNotNull(responseEntity);
        // Verify that delete was not called
        verify(commentRepository, times(1)).findById(commentId);
    }
}
