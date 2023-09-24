package mju.chatuniv.chat.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import mju.chatuniv.auth.service.AuthService;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.chat.domain.chat.ChatRepository;
import mju.chatuniv.chat.domain.chat.Conversation;
import mju.chatuniv.chat.domain.chat.ConversationRepository;
import mju.chatuniv.helper.integration.IntegrationTest;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.domain.MemberRepository;
import mju.chatuniv.member.service.dto.MemberRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
        authService.register(new MemberRequest("a@a.com", "1234"));
        member = memberRepository.findByEmail("a@a.com").get();
        MemberRequest memberRequest = new MemberRequest("a@a.com", "1234");
        token = authService.login(memberRequest);
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
        Conversation conversation = Conversation.of("ask", "answer", chat);
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
