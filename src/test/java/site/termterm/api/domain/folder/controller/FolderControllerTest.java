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
        // folder1 : {1, 2} , folder2 : {1, 5, 3}

        Member sinner = newMember("1111", "sinner@gmail.com");
        sinner.addFolderLimit();       // 폴더 생성 가능 횟수 : 4
        memberRepository.save(sinner);

        Folder folder1 = newFolder("새 폴더1", "새 폴더 설명1", sinner);
        Folder folder2 = newFolder("새 폴더2", "새 폴더 설명2", sinner);
        Term term1 = termRepository.save(newTerm("용어1", "용어1 설명", List.of(CategoryEnum.IT)));
        Term term2 = termRepository.save(newTerm("용어2", "용어2 설명", List.of(CategoryEnum.IT)));
        Term term3 = termRepository.save(newTerm("용어3", "용어3 설명", List.of(CategoryEnum.IT)));
        Term term4 = termRepository.save(newTerm("용어4", "용어4 설명", List.of(CategoryEnum.IT)));
        Term term5 = termRepository.save(newTerm("용어5", "용어5 설명", List.of(CategoryEnum.IT)));

        folder1.getTermIds().add(term1.getId());
        folder1.getTermIds().add(term2.getId());
        folder2.getTermIds().add(term1.getId());
        folder2.getTermIds().add(term5.getId());
        folder2.getTermIds().add(term3.getId());
        folderRepository.save(folder1);
        folderRepository.save(folder2);

        termBookmarkRepository.save(newTermBookmark(term1, sinner, 2));
        termBookmarkRepository.save(newTermBookmark(term2, sinner, 1));
        termBookmarkRepository.save(newTermBookmark(term3, sinner, 1));
        termBookmarkRepository.save(newTermBookmark(term5, sinner, 1));

        /*  *******************************************************************
        * folder3 : {6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16}                 */


        Folder folder3 = newFolder("새 폴더3", "새 폴더 설명3", sinner);

        Term term6 = termRepository.save(newTerm("용어6", "용어6 설명", List.of(CategoryEnum.IT)));
        Term term7 = termRepository.save(newTerm("용어7", "용어7 설명", List.of(CategoryEnum.IT)));
        Term term8 = termRepository.save(newTerm("용어8", "용어8 설명", List.of(CategoryEnum.IT)));
        Term term9 = termRepository.save(newTerm("용어9", "용어9 설명", List.of(CategoryEnum.IT)));
        Term term10 = termRepository.save(newTerm("용어10", "용어10 설명", List.of(CategoryEnum.IT)));
        Term term11 = termRepository.save(newTerm("용어11", "용어11 설명", List.of(CategoryEnum.IT)));
        Term term12 = termRepository.save(newTerm("용어12", "용어12 설명", List.of(CategoryEnum.IT)));
        Term term13 = termRepository.save(newTerm("용어13", "용어13 설명", List.of(CategoryEnum.IT)));
        Term term14 = termRepository.save(newTerm("용어14", "용어14 설명", List.of(CategoryEnum.IT)));
        Term term15 = termRepository.save(newTerm("용어15", "용어15 설명", List.of(CategoryEnum.IT)));
        Term term16 = termRepository.save(newTerm("용어16", "용어16 설명", List.of(CategoryEnum.IT)));

        List<Term> terms = List.of(term6, term7, term8, term9, term10, term11, term12, term13, term14, term15, term16);
        for (int i=0; i<terms.size(); i++){
            folder3.getTermIds().add((long) i+1);
            termBookmarkRepository.save(newTermBookmark(terms.get(i), sinner, 1));
        }
        folderRepository.save(folder3);

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
        requestDto.setTermId(4L);

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

        //when 1
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                delete("/v2/s/folder/{folderId}", "1"));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then 1
        resultActions.andExpect(status().isOk());
        Optional<Folder> folder2 = folderRepository.findById(2L);
        assertThat(folderRepository.findById(1L)).isEqualTo(Optional.empty());
        assertThat(folder2).isNotEqualTo(Optional.empty());
        assertThat(folder2.get().getTermIds().size()).isEqualTo(3);

        //when 2
        ResultActions resultActions2 = mvc.perform(
                delete("/v2/s/folder/{folderId}", "2"));

        //then 2
        resultActions2.andExpect(status().isOk());
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
        resultActions.andExpect(status().isOk());
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
        resultActions.andExpect(status().isOk());
        assertThat(termBookmarkRepository.findByTermAndMember(newMockTerm(2L, "", "", null), newMockMember(1L, "", "")))
                .isEqualTo(Optional.empty());

    }

    @DisplayName("폴더 상세 정보 보기 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void folder_details_success_test() throws Exception{
        //given
        // folder2 에는 [1, 5, 3] 이 있음
        Long folderId = 2L;

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                get("/v2/s/folder/detail/sum/{folderId}", folderId));   //
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("내 폴더 리스트 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void my_folder_list_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/list"));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());

    }

    @DisplayName("폴더 관련 정보 (모달) API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void folder_related_info_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/related-info"));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.currentFolderCount").value(3));
        resultActions.andExpect(jsonPath("$.data.myFolderCreationLimit").value(4));
        resultActions.andExpect(jsonPath("$.data.systemFolderCreationLimit").value(9));
    }

    @DisplayName("아카이빙한 용어들 중 최대 10개를 랜덤으로 뽑아 줄 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void random_10_archived_terms_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/term/random-10"));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.length()").value(10));
    }

    @DisplayName("폴더에 특정 단어 포함 여부 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void folder_is_including_term_success_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/{folderId}/including/{termId}", "1", "1"));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isExist").value("true"));

    }

    @DisplayName("폴더에 특정 단어 포함 여부 API 요청 - 실패")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void folder_is_including_term_fail_test() throws Exception{
        //given

        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/{folderId}/including/{termId}", "1", "3"));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.isExist").value("false"));

    }

}