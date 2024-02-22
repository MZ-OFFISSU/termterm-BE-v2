package site.termterm.api.domain.inquiry.controller;

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
import site.termterm.api.domain.inquiry.entity.Inquiry;
import site.termterm.api.domain.inquiry.entity.InquiryType;
import site.termterm.api.domain.inquiry.repository.InquiryRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import static site.termterm.api.domain.inquiry.dto.InquiryRequestDto.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class InquiryControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @BeforeEach
    public void setUp(){
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
    }

    @DisplayName("문의사항 등록 API")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_inquiry_test() throws Exception{
        //given
        String email = "register_inquiry@test.com";
        String content = "문의 드려요";

        InquiryRegisterRequestDto requestDto = new InquiryRegisterRequestDto();
        requestDto.setContent(content);
        requestDto.setType("OTHER");
        requestDto.setEmail(email);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/inquiry")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

        List<Inquiry> inquiryPSList = inquiryRepository.findByEmail(email);
        assertThat(inquiryPSList.size()).isEqualTo(1);
        assertThat(inquiryPSList.get(0).getType()).isEqualTo(InquiryType.OTHER);
        assertThat(inquiryPSList.get(0).getContent()).isEqualTo(content);

    }

    @DisplayName("문의사항 등록 API - 유효성 검사 실패")
    @Test
    public void register_inquiry_validation_fail_test() throws Exception{
        //given
        InquiryRegisterRequestDto requestDto = new InquiryRegisterRequestDto();
        requestDto.setContent("");
        requestDto.setType("TER");
        requestDto.setEmail("YES!");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/inquiry")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data.length()").value(3));

    }



}