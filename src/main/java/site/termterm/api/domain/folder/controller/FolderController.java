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

}
