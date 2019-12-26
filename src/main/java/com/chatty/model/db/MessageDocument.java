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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@Document(indexName = "message", type = "message")
public class MessageDocument {

    @Id
    private UUID id;

    private UUID chatId;

    private Long senderId;

    private Long receiverId;

    private String text;

    private Long timestampMillis;

}
