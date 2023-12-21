package vn.iostar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import vn.iostar.contants.RoleName;
import vn.iostar.dto.GenericResponse;
import vn.iostar.dto.NotificationDTO;
import vn.iostar.entity.*;
import vn.iostar.repository.NotificationRepository;
import vn.iostar.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private PostGroupService postGroupService;

	@Autowired
	private PostService postService;

	@Autowired
	private FriendRequestService friendRequestService;

	@Autowired
	private ShareService shareService;

	@Autowired
	private CommentService commentService;

	@Override
	public void flush() {
		notificationRepository.flush();
	}

	@Override
	public <S extends Notification> S saveAndFlush(S entity) {
		return notificationRepository.saveAndFlush(entity);
	}

	@Override
	public <S extends Notification> List<S> saveAllAndFlush(Iterable<S> entities) {
		return notificationRepository.saveAllAndFlush(entities);
	}

	@Override
	public void deleteAllInBatch(Iterable<Notification> entities) {
		notificationRepository.deleteAllInBatch(entities);
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<String> strings) {
		notificationRepository.deleteAllByIdInBatch(strings);
	}

	@Override
	public void deleteAllInBatch() {
		notificationRepository.deleteAllInBatch();
	}

	@Override
	public Notification getReferenceById(String s) {
		return notificationRepository.getReferenceById(s);
	}

	@Override
	public <S extends Notification> List<S> findAll(Example<S> example) {
		return notificationRepository.findAll(example);
	}

	@Override
	public <S extends Notification> List<S> findAll(Example<S> example, Sort sort) {
		return notificationRepository.findAll(example, sort);
	}

	@Override
	public <S extends Notification> List<S> saveAll(Iterable<S> entities) {
		return notificationRepository.saveAll(entities);
	}

	@Override
	public List<Notification> findAll() {
		return notificationRepository.findAll();
	}

	@Override
	public List<Notification> findAllById(Iterable<String> strings) {
		return notificationRepository.findAllById(strings);
	}

	@Override
	public <S extends Notification> S save(S entity) {
		return notificationRepository.save(entity);
	}

	@Override
	public Optional<Notification> findById(String s) {
		return notificationRepository.findById(s);
	}

	@Override
	public long count() {
		return notificationRepository.count();
	}

	@Override
	public void deleteById(String s) {
		notificationRepository.deleteById(s);
	}

	@Override
	public void delete(Notification entity) {
		notificationRepository.delete(entity);
	}

	@Override
	public void deleteAll() {
		notificationRepository.deleteAll();
	}

	@Override
	public List<Notification> findAll(Sort sort) {
		return notificationRepository.findAll(sort);
	}

	@Override
	public Page<Notification> findAll(Pageable pageable) {
		return notificationRepository.findAll(pageable);
	}

	@Override
	public <S extends Notification> Page<S> findAll(Example<S> example, Pageable pageable) {
		return notificationRepository.findAll(example, pageable);
	}

	@Override
	public <S extends Notification> long count(Example<S> example) {
		return notificationRepository.count(example);
	}

	@Override
	@Transactional
	public NotificationDTO saveNotificationDTO(NotificationDTO notification) {
		Notification entity = new Notification();
		entity.setContent(notification.getContent());
		entity.setCreateAt(notification.getCreateAt());
		entity.setUpdateAt(notification.getUpdateAt());
		entity.setIsRead(notification.getIsRead());
		entity.setLink(notification.getLink());
		entity.setPhoto(notification.getPhoto());
		if (Boolean.TRUE.equals(notification.getIsAdmin())) {
			List<User> user = userService.findByRoleRoleName(RoleName.Admin);
			if (!user.isEmpty()) {
				for (User user2 : user) {
					entity.setUser(user2);
				}
			}
		} else {
			Optional<User> receiver = userService.findById(notification.getUserId());
			receiver.ifPresent(entity::setUser);
		}

		if (notification.getPostId() != null) {
			Optional<Post> post = postService.findById(notification.getPostId());
			post.ifPresent(entity::setPost);
		}
		if (notification.getShareId() != null) {
			Optional<Share> share = shareService.findById(notification.getShareId());
			share.ifPresent(entity::setShare);
		}
		if (notification.getCommentId() != null) {
			Optional<Comment> comment = commentService.findById(notification.getCommentId());
			comment.ifPresent(entity::setComment);
		}
		if (notification.getGroupId() != null) {
			Optional<PostGroup> postGroup = postGroupService.findById(notification.getGroupId());
			postGroup.ifPresent(entity::setPostGroup);
		}
		if (notification.getFriendRequestId() != null) {
			Optional<FriendRequest> friendRequest = friendRequestService.findById(notification.getFriendRequestId());
			friendRequest.ifPresent(entity::setFriendRequest);
		}

		notificationRepository.save(entity);

		return new NotificationDTO(entity);
	}

	@Override
	public ResponseEntity<GenericResponse> getListNotificationByUserId(String userIdToken, Pageable pageable) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			List<Notification> notifications = notificationRepository.findByUserUserIdOrderByCreateAtDesc(userIdToken,
					pageable);
			List<NotificationDTO> list = new ArrayList<>();
			for (Notification notification : notifications) {
				list.add(new NotificationDTO(notification));
			}
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Get list notification successfully!")
							.result(list).statusCode(HttpStatus.OK.value()).build());

		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public ResponseEntity<GenericResponse> readNotification(String userIdToken, String notificationId) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			int checkNotify = notificationRepository.markAsRead(notificationId);
			if (checkNotify > 0) {
				return ResponseEntity.ok(GenericResponse.builder().success(true)
						.message("Readed Notification Successfully!").statusCode(HttpStatus.OK.value()).build());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new GenericResponse(false, "Cannot found notification!", null, HttpStatus.NOT_FOUND.value()));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public ResponseEntity<GenericResponse> deleteNotification(String userIdToken, String notificationId) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			Optional<Notification> notification = notificationRepository.findById(notificationId);
			if (notification.isPresent()) {
				notificationRepository.deleteById(notificationId);
				return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete Successfully!")
						.result(null).statusCode(HttpStatus.OK.value()).build());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new GenericResponse(false, "Cannot found notification!", null, HttpStatus.NOT_FOUND.value()));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public ResponseEntity<GenericResponse> deleteAllNotification(String userIdToken) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			List<Notification> notification = notificationRepository.findByUserUserId(userIdToken);
			if (notification != null) {
				notificationRepository.deleteAll(notification);
				return ResponseEntity.ok(GenericResponse.builder().success(true).message("Delete Successfully!")
						.result(null).statusCode(HttpStatus.OK.value()).build());
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new GenericResponse(false, "Cannot found notification!", null, HttpStatus.NOT_FOUND.value()));
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	@Transactional
	public ResponseEntity<GenericResponse> createNotification(String userIdToken, NotificationDTO notificationDTO) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			NotificationDTO entity = saveNotificationDTO(notificationDTO);
			return ResponseEntity
					.ok(GenericResponse.builder().success(true).message("Retrieving user profile successfully")
							.result(entity).statusCode(HttpStatus.OK.value()).build());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Delete denied!", null, HttpStatus.NOT_FOUND.value()));
		}
	}

	@Override
	public ResponseEntity<GenericResponse> unReadNotification(String userIdToken) {
		Optional<User> user = userService.findById(userIdToken);
		if (user.isPresent()) {
			List<Notification> list = notificationRepository.findAll();
			for (Notification notification2 : list) {
				notification2.setIsRead(true);
				notificationRepository.save(notification2);
			}
			return ResponseEntity.ok(GenericResponse.builder().success(true)
					.message("Readed Notification Successfully!").statusCode(HttpStatus.OK.value()).build());

		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new GenericResponse(false, "Cannot found user!", null, HttpStatus.NOT_FOUND.value()));
		}
	}
}
