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
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

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
    private TermRepository termRepository;

    @Autowired
    private TermBookmarkRepository termBookmarkRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        Member sinner = memberRepository.save(newMember("1111", "sinner@gmail.com"));
        Folder folder1 = newFolder("새 폴더1", "새 폴더 설명1", sinner);
        Folder folder2 = newFolder("새 폴더2", "새 폴더 설명2", sinner);
        Term term1 = termRepository.save(newTerm("용어1", "용어1 설명", List.of(CategoryEnum.IT)));
        Term term2 = termRepository.save(newTerm("용어2", "용어2 설명", List.of(CategoryEnum.IT)));
        Term term3 = termRepository.save(newTerm("용어3", "용어3 설명", List.of(CategoryEnum.IT)));

        folder1.getTermIds().add(term1.getId());
        folder1.getTermIds().add(term2.getId());
        folder2.getTermIds().add(term1.getId());
        folderRepository.save(folder1);
        folderRepository.save(folder2);

        termBookmarkRepository.save(newTermBookmark(term1, sinner, 2));
        termBookmarkRepository.save(newTermBookmark(term2, sinner, 1));

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

    @DisplayName("용어 아카이브 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void archive_term_into_folders_success_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L));
        requestDto.setTermId(3L);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/folder/term")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("폴더 삭제 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_folder_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                delete("/v2/s/folder/{folderId}", "1"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isNoContent());
        Optional<Folder> folder2 = folderRepository.findById(2L);
        assertThat(folderRepository.findById(1L)).isEqualTo(Optional.empty());
        assertThat(folder2).isNotEqualTo(Optional.empty());
        assertThat(folder2.get().getTermIds().size()).isEqualTo(1);


        //when
        ResultActions resultActions2 = mvc.perform(
                delete("/v2/s/folder/{folderId}", "2"));

        //then
        resultActions2.andExpect(status().isNoContent());
        assertThat(folderRepository.findById(2L)).isEqualTo(Optional.empty());

    }


    @DisplayName("폴더 삭제 API 요청 - 유효성 검사 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void delete_folder_validation_fail_test() throws Exception{
        //given

        //when
        ResultActions resultActions = mvc.perform(
                delete("/v2/s/folder/{folderId}", "0"));

        //then
        resultActions.andExpect(status().isBadRequest());

    }

    @DisplayName("용어 아카이브 해제 API 요청 - 성공1")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void unarchive_term_success_test1() throws Exception{
        //given
        // folder1 : {1L, 2L}, folder2 : {1L}
        // termBookmark1 : {1L, 2군데}, termBookmark2 : {2L, 1군데}
        // folder1 에 1L 의 term 을 지우려 한다.
        UnArchiveTermRequestDto requestDto = new UnArchiveTermRequestDto();
        requestDto.setTermId(1L);
        requestDto.setFolderId(1L);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        ResultActions resultActions = mvc.perform(
                delete("/v2/s/folder/term")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isNoContent());
        assertThat(folderRepository.findById(1L).get().getTermIds().size()).isEqualTo(1);

    }

    @DisplayName("용어 아카이브 해제 API 요청 - 성공2")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void unarchive_term_success_test2() throws Exception{
        //given
        // folder1 : {1L, 2L}, folder2 : {1L}
        // termBookmark1 : {1L, 2군데}, termBookmark2 : {2L, 1군데}
        // folder1 에 2L 의 term 을 지우려 한다. 그러면 termBookmark2 가 DB 에서 지워져야 한다.
        UnArchiveTermRequestDto requestDto = new UnArchiveTermRequestDto();
        requestDto.setTermId(2L);
        requestDto.setFolderId(1L);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                delete("/v2/s/folder/term")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isNoContent());
        assertThat(termBookmarkRepository.findByTermAndMember(newMockTerm(2L, "", "", null), newMockMember(1L, "", "")))
                .isEqualTo(Optional.empty());

    }
}