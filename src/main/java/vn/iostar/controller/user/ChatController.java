package vn.iostar.controller.user;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.transaction.annotation.Transactional;
import vn.iostar.dto.MessageDTO;
import vn.iostar.dto.MessageRequest;
import vn.iostar.entity.Message;
import vn.iostar.entity.Notification;
import vn.iostar.entity.User;
import vn.iostar.repository.MessageRepository;
import vn.iostar.service.CloudinaryService;
import vn.iostar.service.MessageService;
import vn.iostar.service.NotificationService;
import vn.iostar.service.UserService;

@Controller
public class ChatController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private MessageService messageService;

	@Autowired
	private MessageRepository messageRepository;

	@Autowired
	private NotificationService notificationService;
	@MessageMapping("/public-message")
	public Message receiveMessage(@Payload Message message) {
		// Lưu tin nhắn vào cơ sở dữ liệu
		messageService.save(message);
		simpMessagingTemplate.convertAndSendToUser(String.valueOf(message.getGroup().getPostGroupId()),"/public", message);
		return message;
	}

    @MessageMapping("/joinRoom")
    public void joinRoom(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Xử lý yêu cầu tham gia vào phòng
		messageService.save(message);
        // Gửi thông báo về việc tham gia vào phòng
        simpMessagingTemplate.convertAndSendToUser(message.getSender().getUserId(), "/public", message.getSender().getUserName()+" Đã được thêm vào nhóm!!!");
    }

    @MessageMapping("/sendMessage/{roomId}")
    public Message sendMessage(@Payload MessageDTO chatMessage, @DestinationVariable String roomId) throws IOException {
        // Xử lý tin nhắn trong phòng
		Message entity = messageService.saveMessageByDTO(chatMessage);

        // Gửi tin nhắn đến tất cả người dùng trong phòng
        simpMessagingTemplate.convertAndSend("/chatroom/room/" + roomId, chatMessage);
		return entity;
	}
    @MessageMapping("/leaveRoom")
    public void leaveRoom(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        // Xử lý yêu cầu rời phòng
        // Gửi thông báo về việc rời phòng
        simpMessagingTemplate.convertAndSendToUser(message.getSender().getUserId(), "/public", message.getSender().getUserName()+" Đã rời nhóm!!!");
    }
	@MessageMapping("/private-message")
	public Message recMessage(@Payload MessageDTO message) throws IOException {

		Message entity = messageService.saveMessageByDTO(message);
		simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
		return entity;
	}
	//Tạo thông báo đến người dùng
//	@MessageMapping("/private-notification")
//	public void notification(@Payload Notification notification) {
//		// Lưu tin nhắn vào cơ sở dữ liệu
//		notification.setCreateAt(new Date());
//		notificationService.save(notification);
//		simpMessagingTemplate.convertAndSendToUser(notification.getUser().getUserId(), "/notify", notification);
//	}
	//Xóa tin nhắn khi nguời dùng xóa tin nhắn
	@MessageMapping("/delete-message")
    @Transactional
	public void deleteMessage(@Payload Message message) {
		// convert Date to Timestamp
		String dateMessage = String.valueOf(message.getCreateAt());
		Timestamp timestamp = Timestamp.valueOf(dateMessage);
		Optional<Message> entity;
		entity = messageRepository.findByCreateAtAndSenderUserIdAndReceiverUserIdAndContent(timestamp,
				message.getSender().getUserId(), message.getReceiver().getUserId(), message.getContent());
        entity.ifPresent(entityMessage -> messageService.delete(entityMessage));
		simpMessagingTemplate.convertAndSendToUser(message.getSender().getUserId(), "/private", message);
	}
	//Xóa thông báo khi nguời dùng xóa thông báo
	@MessageMapping("/delete-notification")
	public void deleteNotification(@Payload Notification notification) {
		notificationService.delete(notification);
	}
	//Gư thông báo đến tất cả người dùng
	@MessageMapping("/notification-all")
	public void notificationAll(@Payload Notification notification) {
		// Lưu tin nhắn vào cơ sở dữ liệu
		notification.setCreateAt(new Date());
		notificationService.save(notification);
		simpMessagingTemplate.convertAndSend("/notification", notification);
	}
	// Gửi tin nhắn vào nhóm riêng tư
	@MessageMapping("/private-group")
	public void privateGroup(@Payload Message message) {
		// Lưu tin nhắn vào cơ sở dữ liệu
		messageService.save(message);
		simpMessagingTemplate.convertAndSendToUser(String.valueOf(message.getGroup().getPostGroupId()), "/private-group", message);
	}
}
