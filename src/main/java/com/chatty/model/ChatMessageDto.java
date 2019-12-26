package com.chatty.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@Builder
@FieldDefaults(level = PRIVATE)
public class ChatMessageDto {

    @Nullable
    private UUID uuid;

    @Nullable
    UUID chatId;

    @NotNull(message = "Sender id can not be null")
    Long senderId;

    @NotNull(message = "Sender userName can not be null")
    String senderUserName;

    @NotNull(message = "Receiver id can not be null")
    Long receiverId;

    @NotNull(message = "Receiver userName can not be null")
    String receiverUserName;

    @NotNull(message = "Text field can not be null")
    @NotEmpty(message = "Text field can not be empty")
    String text;

    @NotNull(message = "Timestamp can not be empty")
    Long timestampMillis;

}
