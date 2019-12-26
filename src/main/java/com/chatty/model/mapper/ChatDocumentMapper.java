package com.chatty.model.mapper;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.db.ChatDocument;
import com.chatty.model.dto.ChatDto;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatDocumentMapper {


    public ChatDocument toFirstUserChatDocument(ChatMessageDto chatMessage) {
        return ChatDocument.builder()
                .id(UUID.randomUUID())
                .chatId(chatMessage.getChatId())
                .ownerUserId(chatMessage.getSenderId())
                .receiverUserId(chatMessage.getReceiverId())
                .receiverUserName(chatMessage.getReceiverUserName())
                .textOfLastMessage(chatMessage.getText())
                .timestampMillisOfLastMessage(chatMessage.getTimestampMillis())
                .build();
    }

    public ChatDocument toSecondUserChatDocument(ChatMessageDto chatMessage) {
        return ChatDocument.builder()
                .id(UUID.randomUUID())
                .chatId(chatMessage.getChatId())
                .ownerUserId(chatMessage.getReceiverId())
                .receiverUserId(chatMessage.getSenderId())
                .receiverUserName(chatMessage.getSenderUserName())
                .textOfLastMessage(chatMessage.getText())
                .timestampMillisOfLastMessage(chatMessage.getTimestampMillis())
                .build();
    }

    public ChatDto toChatDto(ChatDocument chatDocument) {
        return ChatDto.builder()
                .chatId(chatDocument.getChatId())
                .receiverUserId(chatDocument.getReceiverUserId())
                .receiverUserName(chatDocument.getReceiverUserName())
                .textOfLastMessage(chatDocument.getTextOfLastMessage())
                .timestampMillisOfLastMessage(chatDocument.getTimestampMillisOfLastMessage())
                .build();
    }
}
