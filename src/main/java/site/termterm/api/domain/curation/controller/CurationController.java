package site.termterm.api.domain.curation.controller;

import jakarta.persistence.EntityListeners;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.service.CurationService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

import static site.termterm.api.domain.curation.dto.CurationRequestDto.*;
import static site.termterm.api.domain.curation.dto.CurationResponseDto.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v2")
@EntityListeners(AuditingEntityListener.class)  // 이게 있어야만 createdAt, modifiedAt 작동
public class CurationController {
    private final CurationService curationService;

    /**
     * 새로운 큐레이션을 등록합니다. (for ADMIN)
     */
    @PostMapping("/admin/curation/register")
    public ResponseEntity<ResponseDto<?>> registerCurationForAdmin(
            @RequestBody @Valid CurationRegisterRequestDto curationRegisterRequestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginMember loginMember){
        Curation newCuration = curationService.register(curationRegisterRequestDto);

        return new ResponseEntity<>(new ResponseDto<>(1, "", newCuration), HttpStatus.OK);
    }

    /**
     * 큐레이션을 북마크합니다.
     */
    @PutMapping("/s/curation/bookmark/{id}")
    public ResponseEntity<ResponseDto<?>> bookmarkCuration(@PathVariable("id") Long curationId, @AuthenticationPrincipal LoginMember loginMember){
        curationService.bookmark(curationId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "큐레이션 북마크 성공", null), HttpStatus.OK);
    }

    /**
     * 큐레이션 북마크를 취소합니다.
     */
    @PutMapping("/s/curation/unbookmark/{id}")
    public ResponseEntity<ResponseDto<?>> unBookmarkCuration(@PathVariable("id") Long curationId, @AuthenticationPrincipal LoginMember loginMember){

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    /**
     * 큐레이션의 상세정보를 조회합니다.
     */
    @GetMapping("/s/curation/detail/{id}")
    public ResponseEntity<ResponseDto<?>> getCurationDetail(@PathVariable("id") Long curationId, @AuthenticationPrincipal LoginMember loginMember){

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    /**
     * 카테고리별 큐레이션 리스트를 조회합니다.
     */
    @GetMapping("/s/curation/list")
    public ResponseEntity<ResponseDto<?>> getCurationList(
            @RequestParam(value = "category", required = false) String categoryName,
            @AuthenticationPrincipal LoginMember loginMember
    ){

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }

    @GetMapping("/s/curation/archived")
    public ResponseEntity<ResponseDto<?>> getArchivedCuration(@AuthenticationPrincipal LoginMember loginMember){

        return new ResponseEntity<>(new ResponseDto<>(1, "", null), HttpStatus.OK);
    }
}
