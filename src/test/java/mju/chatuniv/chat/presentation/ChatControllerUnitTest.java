package mju.chatuniv.chat.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import mju.chatuniv.auth.application.JwtAuthService;
import mju.chatuniv.chat.application.ChatService;
import mju.chatuniv.chat.application.dto.chat.ChatPromptRequest;
import mju.chatuniv.chat.application.dto.chat.ChattingHistoryResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static mju.chatuniv.fixture.chat.ConversationFixture.createConversation;
import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@AutoConfigureRestDocs
class ChatControllerUnitTest {

    @MockBean
    private ChatService chatService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length}")
    private long validityInMilliseconds;

    @DisplayName("새로운 채팅방을 만든다.")
    @Test
    void make_new_chatting_room() throws Exception {
        // given
        Member member = createMember();
        when(chatService.createNewChattingRoom(member)).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/chats")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member)))
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
        Member member = createMember();

        ChattingHistoryResponse response = ChattingHistoryResponse.from(
                Chat.createDefault(member), List.of(createConversation())
        );

        when(chatService.joinChattingRoom(chatId)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/chats/{chatId}", chatId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + createTokenByMember(member)))
                .andExpect(status().isOk())
                .andDo(customDocument("join_being_chatting_room",
                        requestHeaders(
                                headerWithName("Authorization").description("Basic Auth")
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
                                fieldWithPath("createdAt").description("채팅방 생성 날짜")
                        )
                ));
    }

    @DisplayName("챗봇과 대화한다.")
    @Test
    void use_chat_bot() throws Exception {
        // given
        Long chatId = 1L;
        Member member = createMember();

        Conversation response = createConversation();
        ChatPromptRequest request = new ChatPromptRequest(response.getAsk());

        when(chatService.useRawChatBot(request.getPrompt(), chatId)).thenReturn(response);

        // when & then
        mockMvc.perform(RestDocumentationRequestBuilders.post("/chats/{chatId}", chatId)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(customDocument("use_chat_bot",
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

    private String createTokenByMember(final Member member) {
        Claims claims = Jwts.claims()
                .setSubject(member.getEmail());

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
}
