package site.termterm.api.domain.folder.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import site.termterm.api.domain.folder.entity.Folder;
import site.termterm.api.domain.folder.repository.FolderRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.global.dummy.DummyObject;
import site.termterm.api.global.handler.exceptions.CustomApiException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @DisplayName("폴더 생성 성공")
    @Test
    public void create_new_folder_success_test() throws Exception{
        //given
        FolderCreateRequestDto requestDto = new FolderCreateRequestDto();
        requestDto.setTitle("새 폴더");
        requestDto.setDescription("설명입니다~!");

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

        // stub 2
        Folder newFolder = requestDto.toEntity(sinner);
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

        // stub 1
        Member sinner = newMockMember(1L, "1111", "ema@i.l");
        sinner.getFolders().add(newFolder("1", "1", sinner));
        sinner.getFolders().add(newFolder("1", "1", sinner));
        sinner.getFolders().add(newFolder("1", "1", sinner));
        when(memberRepository.findById(any())).thenReturn(Optional.of(sinner));

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




}