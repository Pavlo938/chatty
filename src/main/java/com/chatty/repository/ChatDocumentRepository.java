package com.chatty.repository;

import com.chatty.model.db.ChatDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Collection;
import java.util.UUID;

public interface ChatDocumentRepository extends ElasticsearchRepository<ChatDocument, UUID> {

    Collection<ChatDocument> findAllByChatId(UUID chatId);

    Page<ChatDocument> findAllByOwnerUserId(Long ownerUserId, Pageable pageable);

}
