package site.termterm.api.domain.folder.controller;

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
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static site.termterm.api.domain.folder.dto.FolderRequestDto.*;

@ExtendWith(DataClearExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class FolderControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        folderRepository.save(newFolder("새 폴더", "새 폴더 설명", sinner));
        em.clear();
    }


    @DisplayName("폴더 생성 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void create_new_folder_success_test() throws Exception{
        //given
        FolderCreateRequestDto requestDto = new FolderCreateRequestDto();
        requestDto.setTitle("새 폴더");
        requestDto.setDescription("새 폴더 설명");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/folder/new")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("폴더 생성 API 요청 - 유효성검사 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void create_new_folder_validation_fail_test() throws Exception{
        //given
        FolderCreateRequestDto requestDto = new FolderCreateRequestDto();
        requestDto.setTitle("10자가 넘는 제목의 폴더 생성을 요청한다.");
        requestDto.setDescription("25자가 넘는 설명의 폴더 생성을 요청한다..........");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                post("/v2/s/folder/new")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data.description").exists());
        resultActions.andExpect(jsonPath("$.data.title").exists());
    }

    @DisplayName("폴더 정보 수정 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_folder_info_success_test() throws Exception{
        //given
        FolderModifyRequestDto requestDto = new FolderModifyRequestDto();
        requestDto.setFolderId(1L);
        requestDto.setName("수정된 폴더");
        requestDto.setDescription("수정된 폴더 설명");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);


        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                put("/v2/s/folder/info")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("폴더 정보 수정 API 요청 - 유효성 검사 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void modify_folder_info_validation_fail_test() throws Exception{
        //given
        FolderModifyRequestDto requestDto = new FolderModifyRequestDto();
        requestDto.setFolderId(1L);
        requestDto.setName("10자가 넘는 제목의 폴더 생성을 요청한다.");
        requestDto.setDescription("25자가 넘는 설명의 폴더 생성을 요청한다..........");

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                put("/v2/s/folder/info")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data.description").exists());
        resultActions.andExpect(jsonPath("$.data.name").exists());
    }





}