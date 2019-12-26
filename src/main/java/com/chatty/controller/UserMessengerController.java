package com.chatty.controller;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.dto.ChatDto;
import com.chatty.model.dto.MessageDto;
import com.chatty.service.UserMessengerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class UserMessengerController {

    private final UserMessengerService userMessengerService;

    @PostMapping("/message")
    public ChatMessageDto send(@RequestBody @Valid ChatMessageDto chatMessage) {

        log.info("send message: {}", chatMessage);
        ChatMessageDto message = userMessengerService.send(chatMessage);
        log.info("returned chatMessage: {} ", message);

        return message;
    }

    @GetMapping("/messages")
    public Page<MessageDto> getAllMessagesByChatId(@RequestParam UUID chatId, Pageable pageable) {

        log.info("getAllMessagesByChatId: {}, with page :{}, and size :{}", chatId,
                pageable.getPageNumber(), pageable.getPageSize());
        Page<MessageDto> allMessagesByChatId = userMessengerService.getAllMessagesByChatId(chatId, pageable);
        log.info("returned getAllMessagesByChatId: {}", allMessagesByChatId.getContent());

        return allMessagesByChatId;
    }

    @GetMapping("/messages/new")
    public Collection<MessageDto> getNewMessagesByChatIdAndTimestampAfter(@RequestParam UUID chatId,
                                                                          @RequestParam Long timestampMillis) {

        log.info("getAllMessagesByChatIdAndTimestampAfter with params: with chatId {}, and timestampMillis {}",
                chatId, timestampMillis);
        Collection<MessageDto> allMessagesByChatIdAndTimestampAfter =
                userMessengerService.getAllMessagesByChatIdAndTimestampAfter(chatId, timestampMillis);
        log.info("returned getAllMessagesByChatIdAndTimestampAfter:  {}", allMessagesByChatIdAndTimestampAfter);

        return allMessagesByChatIdAndTimestampAfter;
    }

    @GetMapping
    public Page<ChatDto> getAllUsersChats(@RequestParam Long userId, Pageable pageable) {

        log.info("getAllUserChats for user :{}, with page :{}, and size :{}",
                userId, pageable.getPageNumber(), pageable.getPageSize());
        Page<ChatDto> allUserChats = userMessengerService.getAllUserChats(userId, pageable);
        log.info("returned getAllUserChats for user :{}", allUserChats);

        return allUserChats;

    }

}
