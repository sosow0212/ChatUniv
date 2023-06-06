package mju.chatuniv.member.service;

import io.restassured.RestAssured;
import mju.chatuniv.member.dto.MemberCreateRequest;
import mju.chatuniv.member.dto.MemberCreateResponse;
import mju.chatuniv.member.entity.Member;
import mju.chatuniv.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init() {
        RestAssured.port = this.port;
    }

    @DisplayName("회원가입을 한다.")
    @Test
    void register_member() {
        // given
        MemberCreateRequest memberCreateRequest = new MemberCreateRequest("a@a.com", "1234");

        // when
        MemberCreateResponse member = memberService.register(memberCreateRequest);

        // then
        Member result = memberRepository.findById(member.getId()).get();
        assertThat(result.getEmail()).isEqualTo(member.getEmail());
    }
}
