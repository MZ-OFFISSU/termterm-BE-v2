package site.termterm.api.domain.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.termterm.api.domain.point.dto.PointResponseDto;
import site.termterm.api.domain.point.service.PointService;
import site.termterm.api.global.config.auth.LoginMember;
import site.termterm.api.global.exception.ResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2")
public class PointController {
    private final PointService pointService;

    /**
     * 사용자의 현재 보유 포인트
     */
    @GetMapping("/s/point/current")
    public ResponseEntity<ResponseDto<Integer>> getCurrentPoint(@AuthenticationPrincipal LoginMember loginMember){
        Integer point = pointService.getCurrentPoint(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "보유 포인트 조회 성공", point), HttpStatus.OK);
    }

    /**
     * 포인트 사용 내역 - paging
     * 정책 상 size = 5
     */
    @GetMapping("/s/point/history")
    public ResponseEntity<ResponseDto<Page<PointResponseDto.PointHistoryResponseDto>>> getPointHistories(Pageable pageable, @AuthenticationPrincipal LoginMember loginMember){
        Page<PointResponseDto.PointHistoryResponseDto> pointHistories = pointService.getPointHistories(pageable, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "포인트 내역 조회 성공", pointHistories), HttpStatus.OK);
    }

    /**
     * 큐레이션 구매
     */
    @GetMapping("/s/point/pay/curation/{id}")
    public ResponseEntity<ResponseDto<?>> payForCuration(@PathVariable(name = "id") Long curationId, @AuthenticationPrincipal LoginMember loginMember){
        pointService.payForCuration(curationId, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, String.format("큐레이션 (id: %s) 구매 성공", curationId), null), HttpStatus.OK);
    }


    /**
     * 폴더 구매
     * 예외 발생 시, 응답 Body 의 data 값
     * -11 : 사용자의 폴더 생성 한도 == 시스템 폴더 개수 한도 == 9
     * -12 : 포인트 부족
     */
    @GetMapping("/s/point/pay/folder")
    public ResponseEntity<ResponseDto<?>> payForFolder(@AuthenticationPrincipal LoginMember loginMember){
        pointService.payForFolder(loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "폴더 구매 성공", null), HttpStatus.OK);
    }
}
