package com.chatty;

import com.chatty.model.ChatMessageDto;
import com.chatty.model.db.ChatDocument;
import com.chatty.model.db.MessageDocument;
import com.chatty.model.dto.MessageDto;
import com.chatty.repository.ChatDocumentRepository;
import com.chatty.repository.MessageDocumentRepository;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static java.util.stream.Stream.of;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@ExtendWith(SpringExtension.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class, MockitoTestExecutionListener.class})
class UserMessengerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatDocumentRepository chatDocumentRepository;

    @Autowired
    private MessageDocumentRepository messageDocumentRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void afterEach() {
        chatDocumentRepository.deleteAll();
        messageDocumentRepository.deleteAll();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForSendingMessage")
    void send(ChatMessageDto chatMessage) {
        String requestJson = objectMapper.writeValueAsString(chatMessage);

        mockMvc.perform(post("/chat/message")
                .contentType(APPLICATION_JSON_UTF8)
                .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.chatId").value("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.senderUserName").value("pasha"))
                .andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.receiverUserName").value("artur"))
                .andExpect(jsonPath("$.text").value("message1"))
                .andExpect(jsonPath("$.timestampMillis").value(1568209142739l)).andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForGettingMessages")
    void getAllMessagesByChatIdAndTimestampAfter(Collection<MessageDocument> messages) {
        messageDocumentRepository.saveAll(messages);

        Collection<MessageDto> expected = objectMapper
                .readValue(new File("src/test/resources/json/responseAllMessagesByChatIdAndTimestamp.json"),
                        Collection.class);

        String expectedJson = objectMapper.writeValueAsString(expected);

        mockMvc.perform(get("/chat/messages/new")
                .param("chatId", "f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6")
                .param("timestampMillis", "1568209207311"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(content().json(expectedJson))
                .andDo(print())
                .andReturn();
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForGetUserChats")
    void getAllUserChats(Collection<ChatDocument> chats) {
        chatDocumentRepository.saveAll(chats);

        mockMvc.perform(get("/chat")
                .param("userId", "1")
                .param("page", "2")
                .param("size", "1")
                .param("sort", "timestampMillisOfLastMessage,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageable.pageSize").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(2))
                .andExpect(jsonPath("$.content.[0].chatId").value("8e9910e4-6228-4d9c-b2c4-dae8a3eb7fa5"))
                .andExpect(jsonPath("$.content.[0].receiverUserId").value(3))
                .andExpect(jsonPath("$.content.[0].receiverUserName").value("pavlo"))
                .andExpect(jsonPath("$.content.[0].textOfLastMessage").value("message1"))
                .andExpect(jsonPath("$.content.[0].timestampMillisOfLastMessage").value(1568209142739l))
                .andDo(print())
                .andReturn();

    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("argumentsForGettingMessages")
    void getAllMessagesByChatId(Collection<MessageDocument> messages) {
        messageDocumentRepository.saveAll(messages);

        mockMvc.perform(get("/chat/messages")
                .param("chatId", "f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6")
                .param("page", "0")
                .param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.[0].uuid").value("d41457cf-8748-4294-8619-ea5bc5409346"))
                .andExpect(jsonPath("$.content.[0].chatId").value("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .andExpect(jsonPath("$.content.[0].senderId").value(1))
                .andExpect(jsonPath("$.content.[0].receiverId").value(2))
                .andExpect(jsonPath("$.content.[0].text").value("message1"))
                .andExpect(jsonPath("$.content.[0].timestampMillis").value(1568209142739l))
                .andExpect(jsonPath("$.content.[1].uuid").value("a0c462f0-935a-47a1-9eba-b24487754d22"))
                .andExpect(jsonPath("$.content.[1].chatId").value("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .andExpect(jsonPath("$.content.[1].senderId").value(1))
                .andExpect(jsonPath("$.content.[1].receiverId").value(2))
                .andExpect(jsonPath("$.content.[1].text").value("message2"))
                .andExpect(jsonPath("$.content.[1].timestampMillis").value(1568209207311l))
                .andDo(print())
                .andReturn();
    }

    private Stream<Arguments> argumentsForSendingMessage() {
        return of(arguments(messageDtoForSending()));
    }

    private ChatMessageDto messageDtoForSending() {
        return ChatMessageDto.builder()
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .senderId(1l)
                .senderUserName("pasha")
                .receiverId(2l)
                .receiverUserName("artur")
                .text("message1")
                .timestampMillis(1568209142739l)
                .build();
    }


    private Stream<Arguments> argumentsForGettingMessages() {
        return of(arguments(setupMessages()));
    }

    private Collection<MessageDocument> setupMessages() {
        Collection<MessageDocument> messages = new ArrayList<>();
        messages.add(MessageDocument.builder()
                .id(fromString("d41457cf-8748-4294-8619-ea5bc5409346"))
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .senderId(1l)
                .receiverId(2l)
                .text("message1")
                .timestampMillis(1568209142739l)
                .build());
        messages.add(MessageDocument.builder()
                .id(fromString("a0c462f0-935a-47a1-9eba-b24487754d22"))
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .senderId(1l)
                .receiverId(2l)
                .text("message2")
                .timestampMillis(1568209207311l)
                .build());
        messages.add(MessageDocument.builder()
                .id(fromString("75c81ef8-426d-4031-af8a-226a1e55c51b"))
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .senderId(2l)
                .receiverId(1l)
                .text("message3")
                .timestampMillis(1568209237706l)
                .build());
        messages.add(MessageDocument.builder()
                .id(fromString("a921f8e8-1029-4cce-aa7f-623be46c2270"))
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .senderId(1l)
                .receiverId(2l)
                .text("message4")
                .timestampMillis(1568209282763l)
                .build());
        messages.add(MessageDocument.builder()
                .id(fromString("c4921a3d-4828-4951-b637-7b34a4c307f4"))
                .chatId(fromString("8e9910e4-6228-4d9c-b2c4-dae8a3eb7fa5"))
                .senderId(1l)
                .receiverId(3l)
                .text("message1")
                .timestampMillis(1568209142739l)
                .build());

        return messages;
    }

    private Stream<Arguments> argumentsForGetUserChats() {
        return of(arguments(userChats()));
    }

    private Collection<ChatDocument> userChats() {
        Collection<ChatDocument> chats = new ArrayList<>();
        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .ownerUserId(1l)
                .receiverUserId(2l)
                .receiverUserName("artur")
                .textOfLastMessage("message4")
                .timestampMillisOfLastMessage(1568209282763l)
                .build());

        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("f19aec8c-eee1-4cf2-bd15-a2495e2fe2b6"))
                .ownerUserId(2l)
                .receiverUserId(1l)
                .receiverUserName("lidiia")
                .textOfLastMessage("message4")
                .timestampMillisOfLastMessage(1568209282763l)
                .build());

        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("8e9910e4-6228-4d9c-b2c4-dae8a3eb7fa5"))
                .ownerUserId(1l)
                .receiverUserId(3l)
                .receiverUserName("pavlo")
                .textOfLastMessage("message1")
                .timestampMillisOfLastMessage(1568209142739l)
                .build());

        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("8e9910e4-6228-4d9c-b2c4-dae8a3eb7fa5"))
                .ownerUserId(3l)
                .receiverUserId(1l)
                .receiverUserName("lidiia")
                .textOfLastMessage("message1")
                .timestampMillisOfLastMessage(1568209142739l)
                .build());

        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("b5d40e7e-6b5e-4ad5-8dd9-b94b36e53bf2"))
                .ownerUserId(1l)
                .receiverUserId(4l)
                .receiverUserName("arkadiy")
                .textOfLastMessage("message1")
                .timestampMillisOfLastMessage(1568295945175l)
                .build());

        chats.add(ChatDocument.builder()
                .id(randomUUID())
                .chatId(fromString("b5d40e7e-6b5e-4ad5-8dd9-b94b36e53bf2"))
                .ownerUserId(4l)
                .receiverUserId(1l)
                .receiverUserName("lidiia")
                .textOfLastMessage("message1")
                .timestampMillisOfLastMessage(1568295945175l)
                .build());

        return chats;
    }

}
