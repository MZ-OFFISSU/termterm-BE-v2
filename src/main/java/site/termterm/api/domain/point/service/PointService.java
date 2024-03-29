package site.termterm.api.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.bookmark.entity.CurationBookmark;
import site.termterm.api.domain.bookmark.repository.CurationBookmarkRepository;
import site.termterm.api.domain.curation.domain.curation_paid.entity.CurationPaid;
import site.termterm.api.domain.curation.domain.curation_paid.repository.CurationPaidRepository;
import site.termterm.api.domain.curation.entity.Curation;
import site.termterm.api.domain.curation.repository.CurationRepository;
import site.termterm.api.domain.member.entity.Member;
import site.termterm.api.domain.member.repository.MemberRepository;
import site.termterm.api.domain.point.entity.PointHistory;
import site.termterm.api.domain.point.entity.PointPaidType;
import site.termterm.api.domain.point.repository.PointHistoryRepository;
import site.termterm.api.global.handler.exceptions.CustomApiException;

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
    private final CurationPaidRepository curationPaidRepository;
    private final CurationRepository curationRepository;
    private final CurationBookmarkRepository curationBookmarkRepository;

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

    /**
     * 큐레이션 구매
     */
    @Transactional
    public Curation payForCuration(Long curationId, Long memberId) {
        // 큐레이션을 구매할 만큼 포인트가 충분히 있는지 확인
        Member memberPS = memberRepository.findById(memberId).orElseThrow(() -> new CustomApiException("사용자가 존재하지 않습니다."));

        if (memberPS.getPoint() < PointPaidType.CURATION.getPoint()){
            throw new CustomApiException("포인트가 부족합니다.");
        }

        // 구매 여부 확인 -> 이미 구매한 큐레이션일 경우 throw
        Optional<CurationPaid> paidOptional = curationPaidRepository.findById(memberId);

        if (paidOptional.isPresent() && paidOptional.get().getCurationIds().contains(curationId)){
            throw new CustomApiException(String.format("사용자 (id : %s)가 이미 큐레이션 (id: %s) 을 구매하였습니다.", memberId, curationId));
        }

        // Curation_Paid 엔티티 생성 후 save
        paidOptional.ifPresentOrElse(
                curationPaid -> curationPaid.getCurationIds().add(curationId),
                () -> curationPaidRepository.save(CurationPaid.builder().id(memberId).curationIds(List.of(curationId)).build()));


        // Point_History 저장, subText 로 큐레이션 제목이 들어간다.
        // of() 메서드의 3번째 인자는 Member 의 이전 포인트이므로, 포인트 차감과의 순서를 반드시 준수해야 한다.
        String curationTitle = curationRepository.getTitleById(curationId);
        pointHistoryRepository.save(PointHistory.of(PointPaidType.CURATION, memberPS, memberPS.getPoint()).setSubText(curationTitle));

        // member 의 포인트 차감
        memberPS.setPoint(memberPS.getPoint() - PointPaidType.CURATION.getPoint());

        // 자동 북마크 처리
        Curation curationPS = curationRepository.getReferenceById(curationId);
        curationBookmarkRepository.save(CurationBookmark.of(curationPS, memberPS));

        return curationPS;
    }

    /**
     * 폴더 구매
     */
    @Transactional
    public void payForFolder(Long memberId){
        Member memberPS = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomApiException("사용자가 존재하지 않습니다."));

        // 사용자의 포인트를 확인하여, 구매 가능 여부 확인
        if (memberPS.getPoint() < PointPaidType.FOLDER.getPoint()){
            throw new CustomApiException("폴더 생성에 필요한 포인트가 부족합니다.", -12);
        }

        // 사용자 폴더 생성 한도 1 추가
        // 동시에 사용자의 폴더 생성 한계가, 시스템 한도와 같은지 여부 확인
        try {
            memberPS.addFolderLimit();
        } catch(RuntimeException e){
            throw new CustomApiException("사용자의 폴더 생성 한도가 최대입니다.", -11);
        }

        // Point History 저장
        pointHistoryRepository.save(PointHistory.of(PointPaidType.FOLDER, memberPS, memberPS.getPoint()));

        // 사용자 포인트 차감
        memberPS.setPoint(memberPS.getPoint() - PointPaidType.FOLDER.getPoint());

    }
}
