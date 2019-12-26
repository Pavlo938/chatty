package com.chatty.repository;

import com.chatty.model.db.MessageDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.UUID;

public interface MessageDocumentRepository extends ElasticsearchRepository<MessageDocument, UUID> {

    Page<MessageDocument> findAllByChatId(UUID chatId, Pageable pageable);

    Collection<MessageDocument> findAllByChatIdAndTimestampMillisAfter(UUID chatId, Long timestampMillis);

}
