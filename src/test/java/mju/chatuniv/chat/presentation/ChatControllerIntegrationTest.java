package mju.chatuniv.chat.presentation;

import io.restassured.RestAssured;
import mju.chatuniv.auth.application.AuthService;
import mju.chatuniv.auth.application.dto.TokenResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.application.dto.MemberCreateRequest;
import mju.chatuniv.member.application.dto.MemberLoginRequest;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ChatControllerIntegrationTest extends IntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    private Member member;

    private String token;

    @BeforeEach
    void setUp() {
        MemberResponse register = authService.register(new MemberCreateRequest("a@a.com", "1234"));
        member = memberRepository.findByEmail(register.getEmail()).get();
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest("a@a.com", "1234");
        TokenResponse tokenResponse = authService.login(memberLoginRequest);
        token = tokenResponse.getAccessToken();
    }

    @DisplayName("채팅방을 생성한다.")
    @Test
    void create_new_chatting_room() {
        // when
        var result = RestAssured.given().log().all()
                .auth().preemptive().oauth2(token)
                .when()
                .post("/chats")
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(result.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(result.header("Location")).isEqualTo("/chats/1")
        );
    }

    @DisplayName("기존 채팅방에 접속한다.")
    @Test
    void join_being_chatting_room() {
        // given
        Chat chat = chatRepository.save(Chat.createDefault(member));
        Conversation conversation = Conversation.from("ask", "answer", chat);
        conversationRepository.save(conversation);

        // when
        var result = RestAssured.given().log().all()
                .auth().preemptive().oauth2(token)
                .when()
                .get("/chats/1")
                .then().log().all()
                .extract();

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
