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
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.db.DataClearExtension;
import site.termterm.api.global.dummy.DummyObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
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
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp(){
        // folder1 : {1, 2} , folder2 : {1, 5, 3}
        Member sinner =memberRepository.save(newMember("1111", "sinner@gmail.com").addFolderLimit());   // 폴더 생성 가능 횟수 : 4
        Member djokovic =memberRepository.save(newMember("1111", "sinner@gmail.com").addFolderLimit());   // 폴더 생성 가능 횟수 : 4

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
        for (int i=6; i<6+terms.size(); i++){
            folder3.getTermIds().add((long) i);
            termBookmarkRepository.save(newTermBookmark(terms.get(i-6), sinner, 1));
        }
        folderRepository.save(folder3);


        // for Full Folder Fail test
        Folder folder4 = newFolder("꽉 찬 폴더4", "꽉 찬 설명4", djokovic);

        for (int i = 100; i<150; i++){
            folder4.getTermIds().add((long) i);
        }
        folderRepository.save(folder4);

        Folder folder5 = newFolder("새 폴더5", "새 폴더 5", djokovic);
        Folder folder6 = newFolder("새 폴더6", "새 폴더 6", djokovic);
        Folder folder7 = newFolder("새 폴더7", "새 폴더 7", djokovic);
        folder5.getTermIds().add(term1.getId());
        folder5.getTermIds().add(term2.getId());

        folder6.getTermIds().add(term1.getId());
        folder6.getTermIds().add(term5.getId());
        folder6.getTermIds().add(term3.getId());

        folderRepository.save(folder5);
        folderRepository.save(folder6);
        folderRepository.save(folder7);



        // for Folder Detail Each test
        Member member1 = memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));
        Member member2 = memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));
        Member member3 = memberRepository.save(newMember("This-is-social-id", "this-is@an.email"));

        Comment comment1 = commentRepository.save(newComment("용어 설명1", "내 머리", member1, term6).addLike().addLike().addLike().setAccepted());
        Comment comment2 = commentRepository.save(newComment("용어 설명2", "내 머리", member2, term6).addLike().setReported());
        Comment comment3 = commentRepository.save(newComment("용어 설명3", "내 머리", member3, term6).setAccepted());

        for (Term term : terms.subList(1, terms.size())){
            commentRepository.save(newComment("comment by member1", "comment source", member1, term).setAccepted());
            commentRepository.save(newComment("comment by member2", "comment source", member2, term).setAccepted());
            commentRepository.save(newComment("comment by member3", "comment source", member3, term).setReported());
        }
        Comment comment8 = commentRepository.save(newComment("용어 설명8", "내 머리", member3, term6));

        commentLikeRepository.save(newMockCommentLike(comment1, sinner, CommentLikeStatus.YES));
        commentLikeRepository.save(newMockCommentLike(comment1, member2, CommentLikeStatus.YES));
        commentLikeRepository.save(newMockCommentLike(comment1, member3, CommentLikeStatus.YES));
        commentLikeRepository.save(newMockCommentLike(comment2, member1, CommentLikeStatus.YES));
        commentLikeRepository.save(newMockCommentLike(comment3, member1, CommentLikeStatus.NO));

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

    @DisplayName("용어 아카이브 API 요청 - 실패 (폴더 꽉 참)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void archive_term_fail_full_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(4L));
        requestDto.setTermId(14L);

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
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data").value(-11));

    }

    @DisplayName("용어 아카이브 API 요청 - 실패 (기저장)")
    @WithUserDetails(value = "2", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void archive_term_fail_already_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(6L, 5L, 7L));
        requestDto.setTermId(1L);

        String requestBody = om.writeValueAsString(requestDto);
        System.out.println(requestBody);

        String title5 = folderRepository.findById(5L).get().getTitle();
        String title6 = folderRepository.findById(6L).get().getTitle();

        //when
        System.out.println(">>>>>>>요청 쿼리 시작");
        ResultActions resultActions = mvc.perform(
                post("/v2/s/folder/term")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));
        System.out.println("<<<<<<<요청 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(jsonPath("$.data").isNotEmpty());
        resultActions.andExpect(jsonPath("$.data.length()").value(2));
        resultActions.andExpect(jsonPath("$.data[0]").value(title5));
        resultActions.andExpect(jsonPath("$.data[1]").value(title6));


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
        assertThat(termBookmarkRepository.findByTermIdAndMember(2L, newMockMember(1L, "", "")))
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
                get("/v2/s/folder/detail/sum/{folderId}", folderId));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());


        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data.terms[0].termId").value(3));
        resultActions.andExpect(jsonPath("$.data.terms[1].termId").value(5));
        resultActions.andExpect(jsonPath("$.data.terms[2].termId").value(1));

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

    @DisplayName("폴더에 포함된 용어들 상세 정보 API 요청 - 성공")
    @WithUserDetails(value = "1", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void folder_detail_each_success_test() throws Exception{
        //given
        Long targetFolderId = 3L;
        int targetFolderSize = folderRepository.findById(targetFolderId).get().getTermIds().size();
        Term term7 = termRepository.findById(7L).get();

        // term7 의 Comment 는 4, 5, 6 이다. 그리고 작성자는 각각 Member1 (2L), Member2 (3L), Member3 (4L) 이다.
        List<Comment> term7_comments = new ArrayList<>();
        for (int i = 4; i < 7; i++){
            term7_comments.add(commentRepository.findById((long) i).get());
        }
        List<Member> term7_comments_authors = List.of(
                memberRepository.findById(3L).get(),
                memberRepository.findById(4L).get(),
                memberRepository.findById(5L).get()
        );


        //when
        System.out.println(">>>>>>>>>>>>>> 쿼리 시작");
        ResultActions resultActions = mvc.perform(get("/v2/s/folder/detail/each/{folderId}", targetFolderId+""));
        System.out.println("<<<<<<<<<<<<<< 쿼리 종료");
        System.out.println(resultActions.andReturn().getResponse().getContentAsString());

        //then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(jsonPath("$.data", hasSize(targetFolderSize)));
        resultActions.andExpect(jsonPath("$.data[0].comments", hasSize(3)));
        resultActions.andExpect(jsonPath("$.data[0].comments[0].liked").value(CommentLikeStatus.YES.toString()));
        resultActions.andExpect(jsonPath("$.data[0].comments[1].liked").value(CommentLikeStatus.NO.toString()));
        resultActions.andExpect(jsonPath("$.data[0].comments[2].liked").value(CommentLikeStatus.NO.toString()));

        resultActions.andExpect(jsonPath("$.data[1].id").value(term7.getId()));

        for (int i = 0; i<3; i++) {
            resultActions.andExpect(jsonPath(String.format("$.data[1].comments[%s].id", i)).value(term7_comments.get(i).getId()));
            resultActions.andExpect(jsonPath(String.format("$.data[1].comments[%s].content", i)).value(term7_comments.get(i).getContent()));
            resultActions.andExpect(jsonPath(String.format("$.data[1].comments[%s].authorName", i)).value(term7_comments_authors.get(i).getNickname()));
            resultActions.andExpect(jsonPath(String.format("$.data[1].comments[%s].authorJob", i)).value(term7_comments_authors.get(i).getJob()));
        }

    }


}