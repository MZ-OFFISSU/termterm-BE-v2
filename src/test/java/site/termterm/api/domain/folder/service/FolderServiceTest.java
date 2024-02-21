package site.termterm.api.domain.folder.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.bookmark.entity.TermBookmark;
import site.termterm.api.domain.bookmark.repository.TermBookmarkRepository;
import site.termterm.api.domain.category.CategoryEnum;
import site.termterm.api.domain.comment.entity.Comment;
import site.termterm.api.domain.comment.repository.CommentRepository;
import site.termterm.api.domain.comment_like.entity.CommentLikeStatus;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.term.dto.TermResponseDto;
import site.termterm.api.domain.term.entity.Term;
import site.termterm.api.domain.term.repository.TermRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static site.termterm.api.domain.folder.dto.FolderRequestDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FolderServiceTest extends DummyObject {
    @InjectMocks
    private FolderService folderService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private TermRepository termRepository;

    @Mock
    private TermBookmarkRepository termBookmarkRepository;

    @Mock
    private CommentRepository commentRepository;

    @DisplayName("폴더 생성 성공")
    @Test
    public void create_new_folder_success_test() throws Exception{
        //given
        FolderCreateRequestDto requestDto = new FolderCreateRequestDto();
        requestDto.setTitle("새 폴더");
        requestDto.setDescription("설명입니다~!");

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder newFolder = requestDto.toEntity(sinner);

        // stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(memberRepository.findFolderLimitById(any())).thenReturn(3);
        when(folderRepository.save(any())).thenReturn(newFolder);

        //when
        FolderCreateResponseDto responseDto = folderService.createNewFolder(requestDto, 1L);
        System.out.println(responseDto);

        //then
        assertThat(responseDto.getFolderName()).isEqualTo("새 폴더");

    }

    @DisplayName("폴더 생성 실패 - 최대 생성 제한 초과")
    @Test
    public void create_new_folder_limit_fail_test() throws Exception {
        //given
        FolderCreateRequestDto requestDto = new FolderCreateRequestDto();
        requestDto.setTitle("새 폴더");
        requestDto.setDescription("설명입니다~!");

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        sinner.getFolders().add(newFolder("1", "1", sinner));
        sinner.getFolders().add(newFolder("1", "1", sinner));
        sinner.getFolders().add(newFolder("1", "1", sinner));

        // stub
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(memberRepository.findFolderLimitById(any())).thenReturn(sinner.getFolderLimit());
        when(folderRepository.countByMemberId(any())).thenReturn(sinner.getFolders().size());

        //then
        assertThrows(CustomApiException.class, () -> folderService.createNewFolder(requestDto, 1L));
    }

    @DisplayName("폴더 정보 수정 성공")
    @Test
    public void modify_folder_info_success_test() throws Exception{
        //given
        FolderModifyRequestDto requestDto = new FolderModifyRequestDto();
        requestDto.setFolderId(1L);
        requestDto.setName("수정된 폴더");
        requestDto.setDescription("수정된 폴더 설명");

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(folderRepository.findById(any())).thenReturn(Optional.of(newMockFolder(1L, "새 폴더", "새 폴더 설명", sinner)));

        //when
        Folder modifiedFolder = folderService.modifyFolderInfo(requestDto, 1L);

        //then
        assertThat(modifiedFolder.getTitle()).isEqualTo("수정된 폴더");
        assertThat(modifiedFolder.getDescription()).isEqualTo("수정된 폴더 설명");
    }

    @DisplayName("폴더 정보 수정 실패 - 소유자 다름")
    @Test
    public void modify_folder_info_different_master_fail_test() throws Exception{
        //given
        FolderModifyRequestDto requestDto = new FolderModifyRequestDto();
        requestDto.setFolderId(1L);
        requestDto.setName("수정된 폴더");
        requestDto.setDescription("수정된 폴더 설명");

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(folderRepository.findById(any())).thenReturn(Optional.of(newMockFolder(1L, "새 폴더", "새 폴더 설명", sinner)));

        //when

        //then
        assertThrows(CustomApiException.class, () -> folderService.modifyFolderInfo(requestDto, 9999L));

    }

    @DisplayName("폴더 용어 저장 - 성공 - 첫 북마크")
    @Test
    public void archive_term_first_into_folders_success_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L, 3L));
        requestDto.setTermId(1L);

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));

        // stub1
        when(folderRepository.findById(1L)).thenReturn(Optional.of(newMockFolder(1L, "폴더1", "폴더 설명1", sinner)));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(newMockFolder(2L, "폴더2", "폴더 설명2", sinner)));
        when(folderRepository.findById(3L)).thenReturn(Optional.of(newMockFolder(3L, "폴더3", "폴더 설명3", sinner)));

        //stub2
        when(termBookmarkRepository.findByTermIdAndMember(any(), any())).thenReturn(Optional.empty());

        //stub3
        when(termRepository.getReferenceById(any())).thenReturn(term);

        //stub4
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);

        //stub5
        when(termBookmarkRepository.save(any())).thenReturn(TermBookmark.of(term.getId(), sinner, requestDto.getFolderIds().size()));

        //when
        TermBookmark termBookmark = folderService.archiveTerm(requestDto, 1L);
        System.out.println(termBookmark);

        //then
        assertThat(termBookmark.getTermId()).isEqualTo(term.getId());
        assertThat(termBookmark.getMember()).isEqualTo(sinner);
        assertThat(termBookmark.getFolderCnt()).isEqualTo(3);
    }

    @DisplayName("폴더 용어 저장 - 성공 - 두번쨰 이상 북마크")
    @Test
    public void archive_term_not_first_into_folders_success_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L, 3L));
        requestDto.setTermId(1L);

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Term term = newMockTerm(1L, "용어1", "용어설명1", List.of(CategoryEnum.IT));

        // stub1
        when(folderRepository.findById(1L)).thenReturn(Optional.of(newMockFolder(1L, "폴더1", "폴더 설명1", sinner)));
        when(folderRepository.findById(2L)).thenReturn(Optional.of(newMockFolder(2L, "폴더2", "폴더 설명2", sinner)));
        when(folderRepository.findById(3L)).thenReturn(Optional.of(newMockFolder(3L, "폴더3", "폴더 설명3", sinner)));

        //stub2
        when(termBookmarkRepository.findByTermIdAndMember(any(), any())).thenReturn(Optional.of(TermBookmark.of(term.getId(), sinner, 2)));

        //stub3
        when(termRepository.getReferenceById(any())).thenReturn(term);

        //stub4
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);

        //when
        TermBookmark termBookmark = folderService.archiveTerm(requestDto, 1L);
        System.out.println(termBookmark);

        //then
        assertThat(termBookmark.getTermId()).isEqualTo(term.getId());
        assertThat(termBookmark.getMember()).isEqualTo(sinner);
        assertThat(termBookmark.getFolderCnt()).isEqualTo(5);
    }

    @DisplayName("폴더 용어 저장 - 실패 - 폴더 꽉 참")
    @Test
    public void archive_term_into_folders_full_fail_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L));
        requestDto.setTermId(1L);

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder1 = newMockFolder(1L, "폴더1", "폴더 설명1", sinner);

        for (int i = 0; i < 50; i++){
            folder1.getTermIds().add((long) (i + 100));
        }
        System.out.println(folder1.getTermIds().size());

        // stub1
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder1));

        //when

        //then
        assertThrows(CustomApiException.class, () -> folderService.archiveTerm(requestDto, 1L));

    }

    @DisplayName("폴더 용어 저장 - 실패 - 기저장된 용어")
    @Test
    public void archive_term_into_folders_already_archived_fail_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L));
        requestDto.setTermId(1L);

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더1", "폴더 설명1", sinner);
        folder.getTermIds().add(1L);

        // stub1
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        //when

        //then
        assertThrows(CustomApiException.class, () -> folderService.archiveTerm(requestDto, 1L));

    }

    @DisplayName("폴더 용어 저장 - 실패 - 소유자 다름")
    @Test
    public void archive_term_into_folders_not_master_fail_test() throws Exception{
        //given
        ArchiveTermRequestDto requestDto = new ArchiveTermRequestDto();
        requestDto.setFolderIds(List.of(1L, 2L));
        requestDto.setTermId(1L);

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더1", "폴더 설명1", sinner);

        // stub1
        when(folderRepository.findById(1L)).thenReturn(Optional.of(folder));

        //when

        //then
        assertThrows(CustomApiException.class, () -> folderService.archiveTerm(requestDto, 2L));

    }

    @DisplayName("폴더 삭제 - 성공")
    @Test
    public void delete_folder_success_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더", "폴더 설명", sinner);
        Term term = newMockTerm(1L, "용어", "용어 설명", List.of(CategoryEnum.IT));
        folder.getTermIds().add(1L);
        TermBookmark termBookmark = TermBookmark.of(term.getId(), sinner, 3);

        //stub1
        when(folderRepository.findById(any())).thenReturn(Optional.of(folder));

        //stub2
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);

        //stub3
        when(termRepository.getReferenceById(any())).thenReturn(term);

        //stub4
        when(termBookmarkRepository.findByTermIdAndMember(any(), any())).thenReturn(Optional.of(termBookmark));

        //when
        folderService.deleteFolder(1L, 1L);

        //then
        assertThat(termBookmark.getFolderCnt()).isEqualTo(2);
    }

    @DisplayName("폴더에서 용어 삭제 - 성공")
    @Test
    public void unarchive_term_from_folder_success_test() throws Exception{
        //given
        // folder 에는 1, 2번 Term 이 들어있고,
        // 1번 term 은 3군데의 폴더에, 2번 term 은 1군데의 폴더에 있다.
        // folder 에 1번 term 을 삭제하려고 한다.
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더", "폴더 설명", sinner);
        Term term1 = newMockTerm(1L, "용어", "용어 설명", List.of(CategoryEnum.IT));
        folder.getTermIds().add(1L);
        folder.getTermIds().add(2L);
        TermBookmark termBookmark1 = TermBookmark.of(term1.getId(), sinner, 3);

        UnArchiveTermRequestDto requestDto = new UnArchiveTermRequestDto();
        requestDto.setFolderId(1L);
        requestDto.setTermId(1L);

        //stub
        when(folderRepository.findById(any())).thenReturn(Optional.of(folder));
        when(termRepository.getReferenceById(any())).thenReturn(term1);
        when(memberRepository.getReferenceById(any())).thenReturn(sinner);
        when(termBookmarkRepository.findByTermIdAndMember(any(), any())).thenReturn(Optional.of(termBookmark1));

        //when
        Folder updatedFolder = folderService.unArchiveTerm(requestDto, 1L);
        System.out.println(updatedFolder);

        //then
        assertThat(updatedFolder.getTermIds().size()).isEqualTo(1);
        assertThat(termBookmark1.getFolderCnt()).isEqualTo(2);

    }

    @DisplayName("폴더 상세 정보 보기 성공")
    @Test
    public void folder_detail_success_test() throws Exception{
        //given
        Long folderId = 1L;
        Long memberId = 1L;

        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더", "폴더 설명", sinner);
        folder.getTermIds().add(1L);

        //stub1
        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));

        //stub2
        when(termRepository.findIdAndNameById(any())).thenReturn(Optional.of(new TermResponseDto.TermIdAndNameResponseDto(1L, "용어")));

        //when
        FolderDetailResponseDto responseDto = folderService.getFolderDetailSum(folderId, memberId);
        System.out.println(responseDto);

        //then
        assertThat(responseDto.getTerms().get(0).getTermId()).isEqualTo(1);

    }

    @DisplayName("내 폴더 리스트 조회 - 성공")
    @Test
    public void my_folder_list_success_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder1 = newMockFolder(1L, "폴더1", "폴더설명1", sinner);
        Folder folder2 = newMockFolder(2L, "폴더2", "폴더설명2", sinner);
        sinner.getFolders().add(folder1);
        sinner.getFolders().add(folder2);


        //stub
        when(memberRepository.findFoldersById(any())).thenReturn(sinner.getFolders());

        //when
        List<FolderMinimumInfoDto> myFolderList = folderService.getMyFolderList(1L);
        System.out.println(myFolderList);

        //then
        assertThat(myFolderList.size()).isEqualTo(2);
        assertThat(myFolderList.get(0).getFolderId()).isEqualTo(1);
        assertThat(myFolderList.get(1).getFolderId()).isEqualTo(2);

    }

    @DisplayName("폴더 관련 정보(모달) 조회 - 성공")
    @Test
    public void folder_related_info_success_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder1 = newMockFolder(1L, "폴더1", "폴더설명1", sinner);
        Folder folder2 = newMockFolder(2L, "폴더2", "폴더설명2", sinner);
        sinner.getFolders().add(folder1);
        sinner.getFolders().add(folder2);

        // stub
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        //when
        FolderRelatedInfoResponseDto folderRelatedInfo = folderService.getFolderRelatedInfo(1L);
        System.out.println(folderRelatedInfo);

        //then
        assertThat(folderRelatedInfo.getCurrentFolderCount()).isEqualTo(2);
        assertThat(folderRelatedInfo.getMyFolderCreationLimit()).isEqualTo(3);
        assertThat(folderRelatedInfo.getSystemFolderCreationLimit()).isEqualTo(9);

    }

    @DisplayName("폴더에 용어 포함 여부 조회 - 성공")
    @Test
    public void folder_is_including_term_success_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        Folder folder = newMockFolder(1L, "폴더1", "폴더설명1", sinner);
        folder.getTermIds().add(1L);
        folder.getTermIds().add(3L);

        //stub
        when(folderRepository.findById(any())).thenReturn(Optional.of(folder));

        //when
        FolderIsIncludingTermResponseDto includingTerm1 = folderService.isIncludingTerm(1L, 1L);
        FolderIsIncludingTermResponseDto includingTerm2 = folderService.isIncludingTerm(1L, 2L);
        FolderIsIncludingTermResponseDto includingTerm3 = folderService.isIncludingTerm(1L, 3L);

        //then
        assertThat(includingTerm1.getIsExist()).isEqualTo(true);
        assertThat(includingTerm2.getIsExist()).isEqualTo(false);
        assertThat(includingTerm3.getIsExist()).isEqualTo(true);

    }

    @DisplayName("폴더 상세페이지_하나씩 보기 성공")
    @Test
    public void folder_detail_each_test() throws Exception{
        //given
        Member sinner = newMockMember(1L, "1111", "ema@i.l");

        Folder folder = newMockFolder(1L, "폴더1", "폴더설명1", sinner);
        folder.getTermIds().add(1L);
        folder.getTermIds().add(3L);

        Term term1 = newMockTerm(1L, "용어1", "용어 설명1", List.of(CategoryEnum.IT));
        Term term3 = newMockTerm(3L, "용어3", "용어 설명3", List.of(CategoryEnum.IT));

        Comment comment = newMockComment(1L, "용어 설명", "내 머리", term1, sinner).addLike();

        List<TermDetailInfoDto> dtoList = List.of(new TermDetailInfoDto(term1), new TermDetailInfoDto(term3));
        List<TermDetailInfoDto.CommentDetailInfoDto> commentDtoList = List.of(new TermDetailInfoDto.CommentDetailInfoDto(comment, sinner.getNickname(), sinner.getJob(), sinner.getProfileImg(), CommentLikeStatus.NO, comment.getTermId()));


        //stub
        when(folderRepository.findById(any())).thenReturn(Optional.of(folder));
        when(termRepository.findTermsByIdListAlwaysBookmarked(any())).thenReturn(dtoList);
        when(commentRepository.getCommentDetailByTermIdList(any(), any(), any(), any())).thenReturn(commentDtoList);

        //when
        List<TermDetailInfoDto> responseDtoList = folderService.getFolderTermDetailEach(1L, 1L);
        System.out.println(responseDtoList);

        //then
        assertThat(responseDtoList.size()).isEqualTo(2);
        assertThat(responseDtoList.get(0).getName()).isEqualTo(term1.getName());
        assertThat(responseDtoList.get(0).getComments().size()).isEqualTo(1);
        assertThat(responseDtoList.get(0).getComments().get(0).getAuthorName()).isEqualTo(sinner.getNickname());

    }


}