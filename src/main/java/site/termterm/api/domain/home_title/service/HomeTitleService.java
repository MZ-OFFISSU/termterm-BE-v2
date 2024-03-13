package site.termterm.api.domain.home_title.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.termterm.api.domain.home_title.entity.HomeSubtitle;
import site.termterm.api.domain.home_title.repository.HomeSubtitleRepository;
import site.termterm.api.domain.member.repository.MemberRepository;

import java.util.List;

import static site.termterm.api.domain.home_title.dto.HomeTitleResponseDto.*;
import static site.termterm.api.domain.home_title.dto.HomeTitleRequestDto.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeTitleService {
    private final HomeSubtitleRepository homeSubtitleRepository;
    private final MemberRepository memberRepository;

    /**
     * 홈 화면 상단 UX Writing 조회
     */
    public HomeTitleByMemberResponseDto getHomeTitle(Long memberId) {
        String nickname = memberRepository.getNicknameById(memberId);
        List<String> subtitleList = homeSubtitleRepository.getNRandom(PageRequest.of(0, 1));

        String subtitle = subtitleList.isEmpty() ? "" : subtitleList.get(0);

        return HomeTitleByMemberResponseDto.of(nickname + "님, 오늘도 화이팅👏", subtitle);
    }

    /**
     * 홈 화면 상단 UX Writing 등록
     */
    @Transactional
    public HomeSubtitle registerHomeSubtitle(HomeSubtitleRegisterRequestDto requestDto) {

        return homeSubtitleRepository.save(requestDto.toEntity());
    }
}
