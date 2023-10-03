package mju.chatuniv.chat.controller;

import static mju.chatuniv.fixture.chat.ConversationFixture.createConversation;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.exception.exceptions.ChattingRoomNotFoundException;
import mju.chatuniv.chat.exception.exceptions.OpenAIErrorException;
import mju.chatuniv.chat.exception.exceptions.OwnerInvalidException;
import mju.chatuniv.chat.infrastructure.dto.ConversationSimpleResponse;
import mju.chatuniv.chat.service.ChatQueryService;
import mju.chatuniv.chat.service.ChatService;
import mju.chatuniv.chat.service.dto.chat.ChatPromptRequest;
import mju.chatuniv.chat.service.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChatController.class)
@AutoConfigureRestDocs
class ChatControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatQueryService chatQueryService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("새로운 채팅방을 만든다.")
    @Test
    void make_new_chatting_room() throws Exception {
        // given
        Member member = Member.of("a@a.com", "password");
        when(chatService.createNewChattingRoom(member)).thenReturn(1L);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(post("/api/chats"))
                .andExpect(status().isCreated())
                .andDo(customDocument(
                        "create_new_chatting_room",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("새로 생성된 채팅방 ID")
                        )
                ));
    }

    @DisplayName("기존에 존재하는 채팅방에 들어간다.")
    @Test
    void join_being_chatting_room() throws Exception {
        // given
        Long chatId = 1L;
        Member member = Member.of("a@a.com", "password");

        ChattingHistoryResponse response = ChattingHistoryResponse.of(
                Chat.createDefault(member),
                List.of(createConversation()),
                true
        );

        when(chatQueryService.joinChattingRoom(any(), any())).thenReturn(response);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/chats/{chatId}", chatId))
                .andExpect(status().isOk())
                .andDo(customDocument("join_being_chatting_room",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("chatId").description("채팅 ID"),
                                fieldWithPath("conversations[0].conversationId").description("대화 Id"),
                                fieldWithPath("conversations[0].content").description("사용자 질문 내용"),
                                fieldWithPath("conversations[0].answer").description("챗봇 답변 내용"),
                                fieldWithPath("conversations[0].createdAt").description("대화 날짜"),
                                fieldWithPath("isOwner").description("채팅 생성자인지 확인하는 필드"),
                                fieldWithPath("createdAt").description("채팅방 생성 날짜")
                        )
                ));
    }

    @DisplayName("질문과 답변으로 채팅방을 검색한다.")
    @Test
    void search_chatting_room() throws Exception {
        // given
        String keyword = "ask";
        List<ConversationSimpleResponse> conversationSimpleResponses = getConversationAllResponse();

        when(chatQueryService.searchChattingRoom(keyword)).thenReturn(conversationSimpleResponses);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/chats/search/{keyword}", keyword))
                .andExpect(status().isOk())
                .andDo(customDocument("search_chatting_room",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("keyword").description("검색어")
                        ),
                        responseFields(
                                fieldWithPath("conversations[0].conversationId").description("대화 Id"),
                                fieldWithPath("conversations[0].ask").description("사용자 질문 내용"),
                                fieldWithPath("conversations[0].answer").description("챗봇 답변 내용"),
                                fieldWithPath("conversations[1].conversationId").description("대화 Id2"),
                                fieldWithPath("conversations[1].ask").description("사용자 질문 내용2"),
                                fieldWithPath("conversations[1].answer").description("챗봇 답변 내용2")
                        )
                ));
    }

    @DisplayName("매운맛 챗봇과 대화한다.")
    @Test
    void use_raw_chat_bot() throws Exception {
        // given
        Long chatId = 1L;
        Member member = Member.of("a@a.com", "password");
        Conversation response = createConversation();
        ChatPromptRequest request = new ChatPromptRequest(response.getAsk());

        when(chatService.useChatBot(anyString(), anyLong(), anyBoolean(), any())).thenReturn(response);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/raw", chatId)), request)
                .andExpect(status().isOk())
                .andDo(customDocument("use_raw_chat_bot",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("conversationId").description("대화 Id"),
                                fieldWithPath("content").description("사용자 질문 내용"),
                                fieldWithPath("answer").description("챗봇 답변 내용"),
                                fieldWithPath("createdAt").description("대화 날짜")
                        )
                ));
    }

    @DisplayName("순한맛 챗봇과 대화한다.")
    @Test
    void use_mild_chat_bot() throws Exception {
        // given
        Long chatId = 1L;
        Member member = Member.of("a@a.com", "password");
        Conversation conversation = createConversation(member);
        ChatPromptRequest request = new ChatPromptRequest(conversation.getAsk());

        when(chatService.useChatBot(anyString(), anyLong(), anyBoolean(), any())).thenReturn(conversation);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/mild", chatId)), request)
                .andExpect(status().isOk())
                .andDo(customDocument("use_mild_chat_bot",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        ),
                        responseFields(
                                fieldWithPath("conversationId").description("대화 Id"),
                                fieldWithPath("content").description("사용자 질문 내용"),
                                fieldWithPath("answer").description("챗봇 답변 내용"),
                                fieldWithPath("createdAt").description("대화 날짜")
                        )
                ));
    }

    @DisplayName("다른 사람의 채팅방을 사용하면 예외가 발생한다.")
    @Test
    void fail_to_use_with_different_member() throws Exception {
        // given
        Long chatId = 1L;
        Member member = Member.of("a@a.com", "password");
        Conversation conversation = createConversation(member);
        ChatPromptRequest request = new ChatPromptRequest(conversation.getAsk());

        when(chatService.useChatBot(anyString(), anyLong(), anyBoolean(), any())).thenThrow(
                new OwnerInvalidException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/mild", chatId)), request)
                .andExpect(status().isUnauthorized())
                .andDo(customDocument("fail_to_use_with_different_member",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        )
                ));
    }

    @DisplayName("존재하지않는 채팅방에 접근하려하면 예외가 발생한다.")
    @Test
    void fail_to_use_with_not_exist_chat_room() throws Exception {
        // given
        Long chatId = 2L;
        Member member = Member.of("a@a.com", "password");
        Conversation conversation = createConversation(member);
        ChatPromptRequest request = new ChatPromptRequest(conversation.getAsk());

        when(chatService.useChatBot(anyString(), anyLong(), anyBoolean(), any())).thenThrow(
                new ChattingRoomNotFoundException(2L));

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/mild", chatId)), request)
                .andExpect(status().isNotFound())
                .andDo(customDocument("fail_to_use_with_not_exist_chat_room",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        )
                ));
    }

    @DisplayName("만약 gpt가 제대로 동작하지 않는다면 예외가 발생한다.")
    @Test
    void fail_to_use_with_wrong_gpt_server() throws Exception {
        // given
        Long chatId = 1L;
        Member member = Member.of("a@a.com", "password");
        Conversation conversation = createConversation(member);
        ChatPromptRequest request = new ChatPromptRequest(conversation.getAsk());

        when(chatService.useChatBot(anyString(), anyLong(), anyBoolean(), any())).thenThrow(new OpenAIErrorException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/mild", chatId)), request)
                .andExpect(status().is5xxServerError())
                .andDo(customDocument("fail_to_use_with_wrong_gpt_server",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        )
                ));
    }

    @DisplayName("채팅 내용이 비어있을 때 예외가 발생한다.")
    @Test
    void fail_to_use_chat_bot_empty_prompt() throws Exception {
        //given
        ChatPromptRequest chatPromptRequest = new ChatPromptRequest("");
        Long chatId = 1L;

        //when & then
        mockTestHelper.createMockRequestWithTokenAndContent((post("/api/chats/{chatId}/mild", chatId)),
                        chatPromptRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_use_chat_bot_empty_prompt",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("chatId").description("채팅방 ID")
                        )
                ));
    }

    @DisplayName("검색어가 없이 검색하면 예외가 발생한다.")
    @Test
    void fail_search_chatting_room_empty_condition() throws Exception {
        // given
        String keyword = null;
        List<ConversationSimpleResponse> conversationSimpleResponses = getConversationAllResponse();

        when(chatQueryService.searchChattingRoom(keyword)).thenReturn(conversationSimpleResponses);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/chats/search/{keyword}", keyword))
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_search_chatting_room_empty_condition",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        pathParameters(
                                parameterWithName("keyword").description("검색어")
                        )
                ));
    }

    private List<ConversationSimpleResponse> getConversationAllResponse() {
        List<ConversationSimpleResponse> conversations = new ArrayList<>();
        LongStream.rangeClosed(1, 2)
                .forEach(index -> {
                    conversations.add(new ConversationSimpleResponse(index, "ask" + index, "answer" + index));
                });
        return conversations;
    }
}
