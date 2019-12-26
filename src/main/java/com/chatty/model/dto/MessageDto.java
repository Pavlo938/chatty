package com.chatty.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    private UUID uuid;

    private UUID chatId;

    private Long senderId;

    private Long receiverId;

    private String text;

    private Long timestampMillis;
}
