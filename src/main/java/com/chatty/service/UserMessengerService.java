package com.chatty.service;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.dto.ChatDto;
import com.chatty.model.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.UUID;

public interface UserMessengerService {

    ChatMessageDto send(ChatMessageDto chatMessage);

    Page<ChatDto> getAllUserChats(Long userId, Pageable pageable);

    Page<MessageDto> getAllMessagesByChatId(UUID chatId, Pageable pageable);

    Collection<MessageDto> getAllMessagesByChatIdAndTimestampAfter(UUID chatId, Long timestampMillis);

}
