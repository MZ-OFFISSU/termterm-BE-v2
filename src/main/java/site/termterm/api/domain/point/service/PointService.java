package site.termterm.api.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.member.repository.MemberRepository;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PointService {
    private final MemberRepository memberRepository;

    /**
     * 사용자의 현재 보유 포인트를 조회합니다.
     */
    public Integer getCurrentPoint(Long memberId) {
        return memberRepository.getPointById(memberId);
    }
}
