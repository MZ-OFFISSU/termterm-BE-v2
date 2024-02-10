package site.termterm.api.domain.folder.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static site.termterm.api.domain.folder.dto.FolderRequestDto.*;
import static site.termterm.api.domain.folder.dto.FolderResponseDto.*;

import site.termterm.api.domain.folder.service.FolderService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2")
public class FolderController {
    private final FolderService folderService;

    /**
     * 새로운 폴더를 생성합니다.
     */
    @PostMapping("/s/folder/new")
    public ResponseEntity<ResponseDto<FolderCreateResponseDto>> createNewFolder(
            @RequestBody @Valid FolderCreateRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        FolderCreateResponseDto responseDto = folderService.createNewFolder(requestDto, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 생성 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 폴더 정보를 수정합니다. (폴더 제목, 설명)
     */
    @PutMapping("/s/folder/info")
    public ResponseEntity<ResponseDto<?>> modifyFolderInfo(
            @RequestBody @Valid FolderModifyRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        folderService.modifyFolderInfo(requestDto, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 정보 수정 성공", null), HttpStatus.OK);
    }

    /**
     * 폴더를 삭제합니다.
     */
    @DeleteMapping("/s/folder/{folderId}")
    public ResponseEntity<ResponseDto<?>> deleteFolder(@PathVariable(name = "folderId") Long folderId, @AuthenticationPrincipal LoginMember loginMember){
        folderService.deleteFolder(folderId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 삭제 성공", null), HttpStatus.OK);
    }

    /**
     * 폴더에 용어를 저장합니다. (아카이빙)
     * 한 번에 여러 폴더에 저장할 수 있습니다.
     * 그러나 폴더에 이미 해당 용어가 담겨 있을 경우, 이미 담은 폴더명들을 리스트로 반환합니다.
     */
    @PostMapping("/s/folder/term")
    public ResponseEntity<ResponseDto<?>> archiveTermIntoFolders(
            @RequestBody @Valid ArchiveTermRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        folderService.archiveTerm(requestDto, loginMember.getMember().getId());
        return new ResponseEntity<>(new ResponseDto<>(1, "용어 아카이브 요청 성공", null), HttpStatus.OK);
    }

    /**
     * 폴더에 용어를 삭제합니다. (아카이빙 해제)
     */
    @DeleteMapping("/s/folder/term")
    public ResponseEntity<ResponseDto<?>> unArchiveTerm(
            @RequestBody @Valid UnArchiveTermRequestDto requestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember
    ){
        folderService.unArchiveTerm(requestDto, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "용어 아카이브 해제 요청 성공", null), HttpStatus.OK);
    }

    /**
     * 폴더 내에 담긴 용어들을 리턴합니다.
     * "폴더 상세 페이지 _ 모아서 보기"
     */
    @GetMapping("/s/folder/detail/sum/{folderId}")
    public ResponseEntity<ResponseDto<FolderDetailResponseDto>> getFolderDetailSum(@PathVariable("folderId") Long folderId, @AuthenticationPrincipal LoginMember loginMember){
        FolderDetailResponseDto responseDto = folderService.getFolderDetailSum(folderId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 상세 조회 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 내 폴더 리스트
     */
    @GetMapping("/s/folder/list")
    public ResponseEntity<ResponseDto<List<FolderMinimumInfoDto>>> getMyFolderList(@AuthenticationPrincipal LoginMember loginMember){
        List<FolderMinimumInfoDto> responseDtoList = folderService.getMyFolderList(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "내 폴더 리스트 조회 성공", responseDtoList), HttpStatus.OK);
    }

    /**
     * 폴더 관련 정보 모달  - 현재 폴더 개수, 나의 폴더 생성 한도, 생성 가능 폴더 개수
     */
    @GetMapping("/s/folder/related-info")
    public ResponseEntity<ResponseDto<FolderRelatedInfoResponseDto>> getFolderRelatedInfo(@AuthenticationPrincipal LoginMember loginMember){
        FolderRelatedInfoResponseDto responseDto = folderService.getFolderRelatedInfo(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 관련 정보 조회 성공", responseDto), HttpStatus.OK);
    }

    /**
     * 홈 화면 - 아카이빙한 용어를 확인해 보세요!
     * 아카이빙한 용어들 중 최대 10개를 랜덤으로 뽑아 리턴
     */
    @GetMapping("/s/folder/term/random-10")
    public ResponseEntity<ResponseDto<?>> getArchivedTermsRandom10(@AuthenticationPrincipal LoginMember loginMember){
        List<TermIdAndNameAndDescriptionDto> responseDtoList = folderService.getArchivedTermsRandom10(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "아카이빙한 용어 최대 10개 리턴 성공", responseDtoList), HttpStatus.OK);
    }
}
