package vn.iostar.controller.user;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import vn.iostar.dto.MessageDTO;
import vn.iostar.dto.ReactDTO;
import vn.iostar.dto.UserDTO;
import vn.iostar.entity.Message;
import vn.iostar.entity.Notification;
import vn.iostar.entity.ReactMessage;
import vn.iostar.service.MessageService;
import vn.iostar.service.NotificationService;
import vn.iostar.service.ReactMessageService;
import vn.iostar.service.UserService;

@Controller
public class ChatController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private MessageService messageService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ReactMessageService reactMessageService;

	@Autowired
	private UserService userService;

	String privateUserMessage = "/private";

	// Nhắn tin giữa các nhóm với nhau
	@MessageMapping("/sendMessage/{roomId}")
	public Message sendMessage(@Payload MessageDTO chatMessage, @DestinationVariable String roomId) throws IOException {
		// Xử lý tin nhắn trong phòng
		Message entity = messageService.saveMessageByDTO(chatMessage);

		// Gửi tin nhắn đến tất cả người dùng trong phòng
		simpMessagingTemplate.convertAndSend("/chatroom/room/" + roomId, chatMessage);
		return entity;
	}

	// Khi kết nối với trang web thì sẽ thay đổi trạng thái của user
	@MessageMapping("/isOnline")
	public void addUser(@Payload UserDTO user) {
		userService.changeOnlineStatus(user);
	}

	// Nhắn tin với giữa các user với nhau
	@MessageMapping("/private-message")
	public Message recMessage(@Payload MessageDTO message) throws IOException {

		Message entity = messageService.saveMessageByDTO(message);
		simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), privateUserMessage, message);
		return entity;
	}

	@MessageMapping("/react-message")
	public ReactMessage reactMessage(@Payload ReactDTO react) throws IOException {
		ReactMessage entity = reactMessageService.saveReactDTO(react);
		if (react.getReactUser().equals(react.getReceiverId())) {
			simpMessagingTemplate.convertAndSendToUser(react.getSenderId(), privateUserMessage, react);
		} else if (react.getSenderId() != null && !"null".equals(react.getSenderId())
				&& react.getReactUser().equals(react.getSenderId()) && react.getReceiverId() != null
				&& react.getReceiverId() != "null") {
			simpMessagingTemplate.convertAndSendToUser(react.getReceiverId(), privateUserMessage, react);
		} else if (react.getGroupId() != null && !"null".equals(react.getGroupId()))
			simpMessagingTemplate.convertAndSend("/chatroom/room/" + react.getGroupId(), react);
		return entity;
	}

	// Tạo thông báo đến người dùng
//	@MessageMapping("/private-notification")
//	public void notification(@Payload Notification notification) {
//		// Lưu tin nhắn vào cơ sở dữ liệu
//		notification.setCreateAt(new Date());
//		notificationService.save(notification);
//		simpMessagingTemplate.convertAndSendToUser(notification.getUser().getUserId(), "/notify", notification);
//	}

	// Xóa thông báo khi nguời dùng xóa thông báo
	@MessageMapping("/delete-notification")
	public void deleteNotification(@Payload Notification notification) {
		notificationService.delete(notification);
	}

	// Gư thông báo đến tất cả người dùng
	@MessageMapping("/notification-all")
	public void notificationAll(@Payload Notification notification) {
		// Lưu tin nhắn vào cơ sở dữ liệu
		notification.setCreateAt(new Date());
		notificationService.save(notification);
		simpMessagingTemplate.convertAndSend("/notification", notification);
	}
}
