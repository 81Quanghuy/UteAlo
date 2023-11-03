package vn.iostar.controller.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import vn.iostar.entity.Message;
import vn.iostar.service.MessageService;

@Controller
public class ChatController {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private MessageService messageService;


	@MessageMapping("/message")
	@SendTo("/chatroom/public")
	public Message receiveMessage(@Payload Message message) {

		// Lưu tin nhắn vào cơ sở dữ liệu
		message.setCreateAt(new Date()); // Đặt thời gian gửi
		messageService.save(message);

		System.out.println(message.toString());
		return message;
	}

	@MessageMapping("/private-message")
	public Message recMessage(@Payload Message message) {
		// Lưu tin nhắn vào cơ sở dữ liệu
		message.setCreateAt(new Date()); // Đặt thời gian gửi
		messageService.save(message);

		simpMessagingTemplate.convertAndSendToUser(message.getReceiverId(), "/private", message);
		System.out.println(message.toString());
		return message;
	}
}
