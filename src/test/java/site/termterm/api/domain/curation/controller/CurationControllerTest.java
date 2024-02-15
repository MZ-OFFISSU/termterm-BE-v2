package site.termterm.api.domain.curation.controller;

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
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class CurationControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private CurationRepository curationRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Member djokovic = memberRepository.save(newMember("1111", "djokovic@gmail.com"));
        Member admin = memberRepository.save(newAdmin());

        em.clear();
    }

    @DisplayName("큐레이션 등록 API 요청 - 성공")
    @WithUserDetails(value = "3", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void register_curation_success_test() throws Exception{
        //given
        CurationRegisterRequestDto requestDto = new CurationRegisterRequestDto();
        requestDto.setTitle("큐레이션 등록 테스트를 위한 큐레이션");
        requestDto.setDescription("설명입니다.");
        requestDto.setThumbnail("www.google.com");
        requestDto.setTermIds(List.of(1L, 3L, 5L, 7L, 9L));
        requestDto.setTags(List.of("tag1", "tag2", "tag3"));
        requestDto.setCategories(List.of("IT", "BUSINESS"));

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/admin/curation/register")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.cnt").value(requestDto.getTermIds().size()));
        resultActions.andExpect(jsonPath("$.data.title").value(requestDto.getTitle()));

    }


}