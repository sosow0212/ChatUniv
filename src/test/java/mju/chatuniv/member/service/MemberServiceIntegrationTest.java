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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static mju.chatuniv.fixture.member.MemberFixture.*;
import static org.junit.jupiter.api.Assertions.*;

@Sql(value = "/data.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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
    public void get_log_in_members_id_and_email(final int id, final String email, final boolean expected) throws Exception {
        //given
        Member member = createMember();

        //when
        MemberResponse response = memberService.getUsingMemberIdAndEmail(member);

        //then
        assertAll(
                () -> assertTrue(response.getMemberId() == id),
                () -> assertTrue(response.getEmail().equals(email))
        );
    }
}
