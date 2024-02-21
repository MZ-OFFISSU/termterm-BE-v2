package site.termterm.api.domain.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<ResponseDto<?>> getPointHistories(Pageable pageable, @AuthenticationPrincipal LoginMember loginMember){
        Page<PointResponseDto.PointHistoryResponseDto> pointHistories = pointService.getPointHistories(pageable, loginMember.getMember().getId());

        return new ResponseEntity<>(new ResponseDto<>(1, "포인트 내역 조회 성공", pointHistories), HttpStatus.OK);
    }

    /**
     * 큐레이션 구매
     */


    /**
     * 폴더 구매
     */
}
