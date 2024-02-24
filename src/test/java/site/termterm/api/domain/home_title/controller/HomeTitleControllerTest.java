package site.termterm.api.domain.home_title.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;
import site.termterm.api.domain.home_title.repository.HomeSubtitleRepository;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import static site.termterm.api.domain.home_title.dto.HomeTitleRequestDto.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class HomeTitleControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HomeSubtitleRepository homeSubtitleRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.save(newMember("1111", "sinner@gmail.com"));
        memberRepository.save(newAdmin());

        homeSubtitleRepository.save(new HomeSubtitle("서브타이틀 1"));
        homeSubtitleRepository.save(new HomeSubtitle("서브타이틀 2"));
        homeSubtitleRepository.save(new HomeSubtitle("서브타이틀 2"));
    }

    @DisplayName("Home Subtitle 조회 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_home_subtitle_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/home/title"));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.mainTitle").isNotEmpty());
        resultActions.andExpect(jsonPath("$.data.subTitle").isNotEmpty());

    }

    @DisplayName("Home Subtitle 등록에 성공한다. - ADMIN")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_subtitle_success_test() throws Exception{
        //given
        String newSubtitle = "서브타이틀!!!12345678989";
        HomeSubtitleRegisterRequestDto requestDto = new HomeSubtitleRegisterRequestDto();
        requestDto.setSubtitle(newSubtitle);

        String requestBody = om.writeValueAsString(requestDto);

        //when
        System.out.println(">>>>>>>>>>>>>>>쿼리 요청 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/admin/subtitle")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<<<쿼리 요청 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        assertThat(homeSubtitleRepository.findBySubtitle(newSubtitle).isPresent()).isTrue();

    }




}