package mju.chatuniv.member.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import mju.chatuniv.board.infrasuructure.dto.BoardReadResponse;
import mju.chatuniv.chat.domain.chat.Chat;
import mju.chatuniv.comment.domain.dto.MembersCommentResponse;
import mju.chatuniv.global.config.ArgumentResolverConfig;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.member.domain.Member;
import mju.chatuniv.member.exception.exceptions.NewPasswordsNotMatchingException;
import mju.chatuniv.member.exception.exceptions.NotCurrentPasswordException;
import mju.chatuniv.member.service.dto.ChangePasswordRequest;
import mju.chatuniv.member.service.service.MemberService;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static mju.chatuniv.fixture.member.MemberFixture.createMember;
import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
class MemberControllerUnitTest {

    @MockBean
    private MemberService memberService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @Autowired
    private MockMvc mockMvc;

    private MockTestHelper mockTestHelper;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("현재 로그인한 회원의 인조키와 이메일을 반환한다.")
    @Test
    void get_using_member_id_and_email() throws Exception {
        //given
        Member member = Member.of("a@a.com", "password");

        given(memberService.getUsingMemberIdAndEmail(any(Member.class))).willReturn(member);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("member_id_and_email",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath(".memberId").description("로그인한 MEMBER의 ID"),
                                fieldWithPath(".email").description("로그인한 MEMBER의 EMAIL")
                        )
                ))
                .andReturn();
    }

    @DisplayName("토큰이 없을 때 현재 회원정보를 조회하면 401에러와 토큰이 없음이 반환된다.")
    @Test
    void fail_to_get_using_member_id_and_email_No_Token() throws Exception {
        // given

        // when & then
        mockTestHelper.createMockRequestWithoutTokenAndContent(get("/api/members"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("현재 비밀번호, 새 비밀번호, 새 비밀번호 재입력을 성공적으로 입력하면 비밀번호가 교체된다. ")
    @Test
    void change_current_members_password() throws Exception {
        //given
        Member member = Member.of("a@a.com", "password");

        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willReturn(member);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.email").value(member.getEmail()))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("change_member_password",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ),
                        responseFields(
                                fieldWithPath("memberId").description("로그인한 MEMBER의 ID"),
                                fieldWithPath("email").description("로그인한 MEMBER의 EMAIL")
                        )
                ));
    }

    @DisplayName("토큰이 없을 경우 401에러와 함께 비밀번호 변경을 실패한다.")
    @Test
    void fail_to_change_password_Unauthorized() throws Exception {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "5678");

        // when & then
        mockTestHelper.createMockRequestWithoutTokenAndWithContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isUnauthorized())
                .andDo(customDocument("fail_to_change_password_Unauthorized",
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("입력한 현재 비밀번호가 기존과 다르면 400에러와 메시지를 반환하며 비밀번호 변경을 실패한다.")
    @Test
    void fail_to_change_password_Not_Current_Password() throws Exception {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("5678", "5678", "5678");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willThrow(NotCurrentPasswordException.class);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_change_password_Not_Current_Password",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("입력한 새로운 비밀번호와 이를 확인하는 비밀번호가 다르면 400에러와 메시지를 반환하며 비밀번호 변경을 실패한다.")
    @Test
    void fail_to_change_password_New_Password_Unmatched() throws Exception {
        //given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "9012");

        given(memberService.changeMembersPassword(any(Member.class), any(ChangePasswordRequest.class)))
                .willThrow(new NewPasswordsNotMatchingException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_change_password_New_Password_Unmatched",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("비밀 번호를 변경할 때 현재 비밀번호가 공백이면 예외가 발생한다.")
    @Test
    void fail_to_change_password_with_empty_current_password() throws Exception {
        // given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("", "5678", "5678");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_change_password_with_empty_current_password",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("비밀 번호를 변경할 때 새로운 비밀번호가 공백이면 예외가 발생한다.")
    @Test
    void fail_to_change_password_with_empty_new_password() throws Exception {
        // given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "", "5678");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_change_password_with_empty_new_password",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("비밀 번호를 변경할 때 새로운 비밀번호를 검증하는 입력이 공백이면 예외가 발생한다.")
    @Test
    void fail_to_change_password_with_empty_new_password_check() throws Exception {
        // given
        ChangePasswordRequest changePasswordRequest =
                new ChangePasswordRequest("1234", "5678", "");

        // when & then
        mockTestHelper.createMockRequestWithTokenAndContent(patch("/api/members"), changePasswordRequest)
                .andExpect(status().isBadRequest())
                .andDo(customDocument("fail_to_change_password_with_empty_new_password_check",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        requestFields(
                                fieldWithPath("currentPassword").description("기존 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호"),
                                fieldWithPath("newPasswordCheck").description("새 비밀번호 재입력")
                        ))).andReturn();
    }

    @DisplayName("회원의 채팅방 내역들을 조회하면 현재 회원이 사용한 채팅방이 리스트로 반환된다.")
    @Test
    void find_current_members_chat_rooms() throws Exception {
        //given
        given(memberService.findMembersChat(any(Member.class)))
                .willReturn(makeDummyChats());

        //when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/members/me/chats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.myChats").isArray())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원의 게시물을 조회하면 게시물의 id, 제목, 내용이 반환된다. ")
    @Test
    void find_current_members_boards() throws Exception {
        //given
        given(memberService.findMembersBoard(any(Member.class))).willReturn(makeDummyBoards());

        //when&then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/members/me/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardResponses").isArray())
                .andExpect(jsonPath("$.boardResponses.length()").value(10))
                .andExpect(jsonPath("$.boardResponses[0].title").value("title0"))
                .andExpect(jsonPath("$.boardResponses[0].content").value("content0"))
                .andExpect(jsonPath("$.boardResponses[0].email").value("em..."))
                .andExpect(jsonPath("$.boardResponses[0].createAt").value("2023-10-09T12:43:47"))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_members_boards",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath(".boardResponses").description("조회시 반환되는 데이터 배열"),
                                fieldWithPath(".boardResponses[0].boardId").description("조회시 반환되는 board의 id"),
                                fieldWithPath(".boardResponses[0].title").description("조회시 반환되는 board의 id"),
                                fieldWithPath(".boardResponses[0].content").description("조회시 반환되는 board의 id"),
                                fieldWithPath(".boardResponses[0].email").description("조회시 반환되는 board의 작성자"),
                                fieldWithPath(".boardResponses[0].createAt").description("조회시 반환되는 board의 생성시간"),
                                fieldWithPath(".boardResponses[0].isMine").description("조회한 사람과 작성한 사람의 일치 여부")
                        )
                ));
    }

    @DisplayName("토큰이 없을 때 게시물을 조회하면 401에러와 토큰이 없음이 반환된다. ")
    @Test
    void fail_to_find_current_members_boards_Unauthorized() throws Exception {
        //given
        given(memberService.findMembersBoard(any(Member.class))).willReturn(makeDummyBoards());

        //when&then
        mockTestHelper.createMockRequestWithoutTokenAndContent(get("/api/members/me/boards"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("회원의 댓글을 조회하면 댓글의 내용, 게시물id, 회원의 이메일이 반환된다. ")
    @Test
    void find_current_members_comments() throws Exception {
        //given
        given(memberService.findMembersComment(any(Member.class))).willReturn(makeDummyComments());

        //when&then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/members/me/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.membersCommentResponses").isArray())
                .andExpect(jsonPath("$.membersCommentResponses.length()").value(10))
                .andExpect(jsonPath("$.membersCommentResponses[0].boardId").value(1))
                .andExpect(jsonPath("$.membersCommentResponses[0].content").value("content0"))
                .andExpect(jsonPath("$.membersCommentResponses[0].email").value("a@a.com"))

                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_members_comments",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath("membersCommentResponses").description("조회시 반환되는 데이터 배열"),
                                fieldWithPath("membersCommentResponses[0].email").description("조회시 반환되는 회원의 이메일"),
                                fieldWithPath("membersCommentResponses[0].boardId").description(
                                        "조회시 반환되는 게시물의 id(onclick이벤트를 위해 반환)"),
                                fieldWithPath("membersCommentResponses[0].content").description("조회시 반환되는 댓글 내용")
                        )
                ));
    }

    @DisplayName("토큰이 없을 때 댓글을 조회하면 401에러와 토큰이 없음이 반환된다. ")
    @Test
    void fail_to_find_current_members_comments_Unauthorized() throws Exception {
        //given
        given(memberService.findMembersComment(any(Member.class))).willReturn(makeDummyComments());

        //when&then
        mockTestHelper.createMockRequestWithoutTokenAndContent(get("/api/members/me/comments"))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    private List<Chat> makeDummyChats() {
        return IntStream.range(0, 10)
                .mapToObj(each -> Chat.createDefault(Member.of("a@a.com", "1234")))
                .collect(Collectors.toList());
    }

    private List<BoardReadResponse> makeDummyBoards() {
        return IntStream.range(0, 10)
                .mapToObj(each -> new BoardReadResponse((long) each, "title" + each, "content" + each, "em...",
                        LocalDateTime.parse("2023-10-09T12:43:47"), true))
                .collect(Collectors.toList());
    }

    private List<MembersCommentResponse> makeDummyComments() {
        return IntStream.range(0, 10)
                .mapToObj(each -> MembersCommentResponse.of(createMember().getEmail(), (long) (each + 1),
                        "content" + each))
                .collect(Collectors.toList());
    }
}

