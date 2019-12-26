package com.chatty.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = PRIVATE)
@Document(indexName = "chat", type = "chat")
public class ChatDocument {

    @Id
    UUID id;

    UUID chatId;

    Long ownerUserId;

    Long receiverUserId;

    String receiverUserName;

    String textOfLastMessage;

    Long timestampMillisOfLastMessage;

}


