package mju.chatuniv.statistic.controller;

import static mju.chatuniv.helper.RestDocsHelper.customDocument;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import mju.chatuniv.auth.service.JwtAuthService;
import mju.chatuniv.global.config.ArgumentResolverConfig;
import mju.chatuniv.helper.MockTestHelper;
import mju.chatuniv.statistic.domain.dto.StatisticResponse;
import mju.chatuniv.statistic.exception.exceptions.StatisticNotFoundException;
import mju.chatuniv.statistic.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@WebMvcTest(StatisticController.class)
@AutoConfigureRestDocs
class StatisticControllerUnitTest {

    private MockTestHelper mockTestHelper;

    @MockBean
    private StatisticService statisticService;

    @MockBean
    private JwtAuthService jwtAuthService;

    @MockBean
    private ArgumentResolverConfig argumentResolverConfig;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockTestHelper = new MockTestHelper(mockMvc);
    }

    @DisplayName("가장 인기있는 검색어 10개를 가져온다.")
    @Test
    void find_statistics() throws Exception {
        // given
        List<StatisticResponse> list = new ArrayList<>();
        list.add(StatisticResponse.of("word",1));
        list.add(StatisticResponse.of("hello",4));
        list.add(StatisticResponse.of("chat",3));

        given(statisticService.findStatistics()).willReturn(list);

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statistics.length()").value(3))
                .andExpect(jsonPath("$.statistics[0].word").value("word"))
                .andExpect(jsonPath("$.statistics[0].frequency").value(1))
                .andDo(MockMvcResultHandlers.print())
                .andDo(customDocument("find_all_statistics",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statistics[0].word").description("실시간 통계 전체 조회 후 반환된 단어"),
                                fieldWithPath("statistics[0].frequency").description("실시간 통계 전체 조회 후 반환된 검색 횟수")
                        )
                )).andReturn();
    }

    @DisplayName("가장 인기있는 검색어가 없는 경우 예외가 발생한다.")
    @Test
    void fail_to_find_statistics_with_not_exist_word() throws Exception {
        // given
        given(statisticService.findStatistics()).willThrow(new StatisticNotFoundException());

        // when & then
        mockTestHelper.createMockRequestWithTokenAndWithoutContent(get("/api/statistics"))
                .andExpect(status().isNotFound())
                .andDo(customDocument("fail_to_find_statistics_with_not_exist_word",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("로그인 후 제공되는 Bearer 토큰")
                        )
                )).andReturn();
    }
}
