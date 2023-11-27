//package Comment;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//import java.util.Date;
//import java.util.Optional;
//import vn.iostar.UtealoApplication;
//import vn.iostar.controller.user.CommentPostController;
//import vn.iostar.dto.GenericResponse;
//import vn.iostar.entity.Comment;
//import vn.iostar.repository.CommentRepository;
//import vn.iostar.security.JwtTokenProvider;
//import vn.iostar.service.CommentService;
//import vn.iostar.service.impl.CommentServiceImpl;
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UtealoApplication.class)
//public class DeleteComentTest {
//	   @Mock
//	    Comment comment;
//
//	    @Mock
//	    CommentRepository commentRepository;
//
//	    @Mock
//	    JwtTokenProvider jwtTokenProvider;
//
//	    @Mock
//	    CommentService commentService = new CommentServiceImpl();
//	    
//	    @Test
//	    public void deleteCommentOfPost_Success() {
//	        // Arrange
//	        int commentId = 1;
//	        Comment comment = new Comment(); // create a Comment object or use a builder if available
//
//	        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
//
//	        // Act
//	        ResponseEntity<GenericResponse> responseEntity = commentService.deleteCommentOfPost(commentId);
//
//	        // Assert
//	     //   assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//	        assertTrue(responseEntity.getBody().getSuccess());
//	        assertEquals("Delete Successful!", responseEntity.getBody().getMessage());
//
//	        // Verify interaction with the commentRepository
//	        verify(commentRepository, times(1)).findById(commentId);
//	       
//	    }
//
//	    @Test
//	    public void deleteCommentOfPost_CommentNotFound() {
//	        // Arrange
//	        int commentId = 1;
//
//	        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
//
//	        // Act
//	        ResponseEntity<GenericResponse> responseEntity = commentService.deleteCommentOfPost(commentId);
//
//	        // Assert
//	      //  assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
//	        assertFalse(responseEntity.getBody().getSuccess());
//	        assertEquals("Cannot found comment!", responseEntity.getBody().getMessage());
//
//	        // Verify that delete was not called
//	        verify(commentRepository, times(1)).findById(commentId);
//	    }
//
//
//}
