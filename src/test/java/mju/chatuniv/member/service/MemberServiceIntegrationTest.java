package mju.chatuniv.member.service;

import io.restassured.RestAssured;
import mju.chatuniv.member.application.dto.MemberResponse;
import mju.chatuniv.member.application.service.MemberService;
import mju.chatuniv.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import static mju.chatuniv.fixture.member.MemberFixture.*;
import static org.assertj.core.api.Assertions.*;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void init(){
        RestAssured.port = this.port;
    }

    @DisplayName("회원 정보는 입력받은 회원으로 만든다.")
    @CsvSource({"1, a@a.com, true", "2, b@b.com, false"})
    @ParameterizedTest
    public void get_my_info(final int id, final String email, final boolean expected) throws Exception{
        //given
        Member member = createMember();

        //when
        MemberResponse response = memberService.getMemberInfo(member);
        //then
        assertThat(response.getMemberId() == id).isEqualTo(expected);
        assertThat(response.getEmail().equals(email)).isEqualTo(expected);
    }
}
