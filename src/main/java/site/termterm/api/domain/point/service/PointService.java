package site.termterm.api.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import static site.termterm.api.domain.point.dto.PointResponseDto.*;
import static site.termterm.api.domain.point.dto.PointResponseDto.PointHistoryResponseDto.*;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointService {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;

    /**
     * 사용자의 현재 보유 포인트를 조회합니다.
     */
    public Integer getCurrentPoint(Long memberId) {
        return memberRepository.getPointById(memberId);
    }

    /**
     * 사용자의 포인트 내역을 조회합니다.
     */
    public Page<PointHistoryResponseDto> getPointHistories(Pageable pageable, Long memberId) {  // TODO: SELECT Member 쿼리가 왜 발생함?
        Member memberPS = memberRepository.getReferenceById(memberId);
        List<PointHistory> pointHistoryList = pointHistoryRepository.findByMemberOrderByDate(memberPS);

        // PointHistory.date 를 기준으로 그룹화, TreeMap 은 Key 에 대하여 정렬이 가능한 Map 이다.
        Map<LocalDate, List<PointHistory>> groupedByDate =
                pointHistoryList.stream().collect(Collectors.groupingBy(PointHistory::getDate, TreeMap::new, Collectors.toList()));


        // 날짜별로 그룹화한 포인트 내역을 토대로 응답 바디 구성
        List<PointHistoryResponseDto> responseDtoList = groupedByDate.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .map(entry -> {
                    List<PointHistoryEachDto> eachDtoList = entry.getValue().stream().map(PointHistoryEachDto::of).sorted(Comparator.reverseOrder()).toList();

                    return builder().date(entry.getKey().format(dateFormatter)).dailyHistories(eachDtoList).build();
                })
                .collect(Collectors.toList());

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), responseDtoList.size());
        return new PageImpl<>(responseDtoList.subList(start, end), pageable, responseDtoList.size());
    }
}
