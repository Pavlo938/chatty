package com.chatty.service.impl;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.db.MessageDocument;
import com.chatty.model.dto.ChatDto;
import com.chatty.model.dto.MessageDto;
import com.chatty.model.mapper.ChatDocumentMapper;
import com.chatty.model.mapper.MessageMapper;
import com.chatty.repository.ChatDocumentRepository;
import com.chatty.repository.MessageDocumentRepository;
import com.chatty.service.UserMessengerService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UserMessengerServiceImpl implements UserMessengerService {

    MessageMapper messageMapper;
    ChatDocumentMapper chatDocumentMapper;
    ChatDocumentRepository chatDocumentRepository;
    MessageDocumentRepository messageDocumentRepository;

    @Override
    public ChatMessageDto send(ChatMessageDto chatMessage) {
        log.info("'send' invoked with chatMessage: {}", chatMessage);

        if (isNull(chatMessage.getChatId())) {
            chatMessage.setChatId(randomUUID());
        }

        if (chatDocumentRepository.findAllByChatId(chatMessage.getChatId()).isEmpty()) {
            chatDocumentRepository.save(chatDocumentMapper.toFirstUserChatDocument(chatMessage));
            chatDocumentRepository.save(chatDocumentMapper.toSecondUserChatDocument(chatMessage));
        }

        chatDocumentRepository.findAllByChatId(chatMessage.getChatId()).stream()
                .map(chat -> chat.toBuilder()
                        .textOfLastMessage(chatMessage.getText())
                        .timestampMillisOfLastMessage(chatMessage.getTimestampMillis())
                        .build()).forEach(chatDocumentRepository::save);

        MessageDocument savedMessage = messageDocumentRepository.save(messageMapper.toMessageEntity(chatMessage));
        log.info("'savedMessage' = {} ",savedMessage);

        log.info("'send' returned with chatMessage: {}", chatMessage);
        return chatMessage;
    }

    @Override
    public Page<MessageDto> getAllMessagesByChatId(UUID chatId, Pageable pageable) {
        log.info("'getAllMessagesByChatId' with chatId: {}, page: {}, size: {}",
                chatId, pageable.getPageNumber(), pageable.getPageSize());

        Page<MessageDto> savedMessage = messageDocumentRepository.findAllByChatId(chatId, pageable)
                .map(messageMapper::toMessageDto);

        log.info("'getAllMessagesByChatId' returned with savedMessage: {}",savedMessage.getContent());
        return savedMessage;
    }

    @Override
    public Collection<MessageDto> getAllMessagesByChatIdAndTimestampAfter(UUID chatId, Long timestampMillis) {
        log.info("'getAllMessagesByChatIdAndTimestampAfter' invoked with chatId: {}, timestampMillis: {}",
                chatId, timestampMillis);

        Collection<MessageDto> allMessages = messageDocumentRepository.findAllByChatIdAndTimestampMillisAfter(chatId, timestampMillis).stream()
                .map(messageMapper::toMessageDto).collect(toList());

        log.info("'getAllMessagesByChatIdAndTimestampAfter' returned with details: {}", allMessages);
        return allMessages;

    }

    @Override
    public Page<ChatDto> getAllUserChats(Long userId, Pageable pageable) {
        log.info("'getAllUserChats' with userId: {}, with page: {}, and size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        Page<ChatDto> allUserChatsDto = chatDocumentRepository.findAllByOwnerUserId(userId, pageable)
                .map(chatDocumentMapper::toChatDto);

        log.info("'getAllUserChats' returned with details: {}", allUserChatsDto);
        return allUserChatsDto;
    }

}
