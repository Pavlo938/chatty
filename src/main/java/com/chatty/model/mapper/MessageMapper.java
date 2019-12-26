package com.chatty.model.mapper;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.db.MessageDocument;
import com.chatty.model.dto.MessageDto;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class MessageMapper {

    public MessageDocument toMessageEntity(ChatMessageDto chatMessage) {
        return MessageDocument.builder()
                .id(randomUUID())
                .chatId(chatMessage.getChatId())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getReceiverId())
                .text(chatMessage.getText())
                .timestampMillis(chatMessage.getTimestampMillis())
                .build();
    }

    public MessageDto toMessageDto(MessageDocument message) {
        return MessageDto.builder()
                .uuid(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .text(message.getText())
                .timestampMillis(message.getTimestampMillis())
                .build();
    }
}
